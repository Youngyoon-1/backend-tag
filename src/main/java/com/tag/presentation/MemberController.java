package com.tag.presentation;

import com.tag.application.MemberImageCategory;
import com.tag.application.MemberInfoCategory;
import com.tag.application.MemberService;
import com.tag.dto.request.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.MemberImageNameUpdateRequest;
import com.tag.dto.request.MemberInfoUpdateRequest;
import com.tag.dto.request.MemberProfileUpdateRequest;
import com.tag.dto.response.MemberDonationInfoResponse;
import com.tag.dto.response.MemberImageGetUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberProfileUpdateResult;
import com.tag.dto.response.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

//    @GetMapping("/api/url")
//    public ResponseEntity<MemberResponse> searchMember2(HttpServletRequest request) {
//        final MemberResponse memberResponse = memberService.findMember(memberId, memberSearchCategory);
//        String referer = request.getHeader("Referer");
//        System.out.println(referer);
//        return ResponseEntity.ok()
//                .body(new MemberResponse("test@test.com",
//                        "헤놀로지 나스를 구축하고 싶어서 많은 자료를 찾아봤지만 감도 못잡고있었습니다.. 무작정 따라해도 성공할 수 있을 정도의 자세하고 친절한 글 덕분에 설치에 성공하고 경험을 통해 기초공부는 충분히 한 것 같네요! 감사합니다~!\n"
//                                + "안녕하세요. 무작정 따라해도 성공할 수 있을 정도의 자세하고 친절한 글 덕분에 설치에 성공하고 경험을 통해 기초공부는 충분히 한 것 같네요! 감사합니다~!",
//                        null, null, "http:localhost"));
//    }

    @GetMapping("/api/members/{memberId}")
    public ResponseEntity<MemberResponse> searchMember(@PathVariable(name = "memberId") final Long memberId,
                                                       // 요청 파라미터가 없는 경우 회원 정보 및 이미지를 모두 제공한다
                                                       @RequestParam(name = "searchCategory", required = false) final String memberSearchCategory) {
        final MemberResponse memberResponse = memberService.findMember(memberId, memberSearchCategory);
        return ResponseEntity.ok()
                .body(memberResponse);
    }

    @PatchMapping("/api/members/me/info")
    public ResponseEntity<MemberInfoUpdateResponse> updateMemberInfo(@AccessTokenValue final Long memberId,
                                                                     @RequestParam(name = "infoCategory") final MemberInfoCategory memberInfoCategory,
                                                                     @RequestBody final MemberInfoUpdateRequest memberInfoUpdateRequest) {
        final String content = memberInfoUpdateRequest.getContent();
        final MemberInfoUpdateResponse memberInfoUpdateResponse = memberService.updateMemberInfo(memberId,
                memberInfoCategory, content);
        return ResponseEntity.ok(memberInfoUpdateResponse);
    }

    @GetMapping("/api/members/{memberId}/donation-info")
    public ResponseEntity<MemberDonationInfoResponse> getMemberDonationInfo(
            @PathVariable(name = "memberId") final Long memberId) {
        final MemberDonationInfoResponse donationInfo = memberService.findDonationInfo(memberId);
        return ResponseEntity.ok()
                .body(donationInfo);
    }

    @PatchMapping("/api/members/me/donation-info")
    public ResponseEntity<MemberInfoUpdateResponse> updateMemberDonationInfo(@AccessTokenValue final Long memberId,
                                                                             @RequestBody final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest) {
        memberService.updateMemberDonationInfo(memberId, memberDonationInfoUpdateRequest);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/api/members/me/profile")
    public ResponseEntity<Void> updateMemberProfile(@AccessTokenValue final Long memberId,
                                                    @RequestBody final MemberProfileUpdateRequest memberProfileUpdateRequest) {
        // 프로필 수정과 s3 이미지 삭제 트랜잭션 분리
        final MemberProfileUpdateResult memberProfileUpdateResult = memberService.updateMemberProfile(memberId,
                memberProfileUpdateRequest);
        memberService.deleteMemberProfileImage(memberProfileUpdateResult);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/api/members/me")
    public ResponseEntity<Void> registerMember(@AccessTokenValue final Long memberId) {
        memberService.registerMember(memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/api/members/me/image-name")
    public ResponseEntity<MemberImageGetUrlResponse> updateImageName(@AccessTokenValue final Long memberId,
                                                                     @RequestParam(name = "imageCategory") final MemberImageCategory memberImageCategory,
                                                                     @RequestBody final MemberImageNameUpdateRequest memberImageNameUpdateRequest) {
        final String imageName = memberImageNameUpdateRequest.getImageName();
        final MemberImageGetUrlResponse memberImageGetUrlResponse = memberService.updateImageName(memberId,
                memberImageCategory,
                imageName);
        return ResponseEntity.ok(memberImageGetUrlResponse);
    }

    @GetMapping("/api/members/{memberId}/profile-image")
    public ResponseEntity<String> getProfileImageUrl(@PathVariable(name = "memberId") final Long memberId) {
        final String profileImageUrl = memberService.getProfileImageUrl(memberId);
        return ResponseEntity.ok()
                .body(profileImageUrl);
    }

    @GetMapping("/api/members/{memberId}/mail-notification")
    public ResponseEntity<Boolean> IsConfirmedMailNotification(@PathVariable(name = "memberId") final Long memberId) {
        final Boolean isConfirmedMailNotification = memberService.IsConfirmedMailNotification(memberId);
        return ResponseEntity.ok()
                .body(isConfirmedMailNotification);
    }

    @PatchMapping("/api/members/me/mail-notification")
    public ResponseEntity<Void> updateIsConfirmedMailNotification(@AccessTokenValue final Long memberId,
                                                                  @RequestParam("isConfirmed") final Boolean isConfirmed) {
        memberService.updateConfirmedMailNotification(memberId, isConfirmed);
        return ResponseEntity.noContent()
                .build();
    }
}
