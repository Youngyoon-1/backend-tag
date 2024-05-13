package com.tag.presentation.member;

import com.tag.application.async.AsyncExecutor;
import com.tag.application.member.MemberService;
import com.tag.dto.request.member.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.member.MemberProfileUpdateRequest;
import com.tag.dto.response.member.MemberProfileUpdateResult;
import com.tag.dto.response.member.MemberResponse;
import com.tag.presentation.auth.AccessTokenValue;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public final class MemberController {

    private final MemberService memberService;
    private final AsyncExecutor asyncExecutor;

    public MemberController(final MemberService memberService, final AsyncExecutor asyncExecutor) {
        this.memberService = memberService;
        this.asyncExecutor = asyncExecutor;
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> searchMember(@PathVariable(name = "memberId") final long memberId,
                                                       @RequestParam(name = "searchCategories") final List<String> memberSearchCategories) {
        final MemberResponse memberResponse = memberService.findMember(memberId, memberSearchCategories);
        return ResponseEntity.ok()
                .body(memberResponse);
    }

    @PatchMapping("/me/donation-info")
    public ResponseEntity<Void> updateMemberDonationInfo(@AccessTokenValue final long memberId,
                                                         @RequestBody @Valid final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest) {
        memberService.updateMemberDonationInfo(memberId, memberDonationInfoUpdateRequest);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<Void> updateMemberProfile(@AccessTokenValue final long memberId,
                                                    @RequestBody @Valid final MemberProfileUpdateRequest memberProfileUpdateRequest) {
        final MemberProfileUpdateResult memberProfileUpdateResult = memberService.updateMemberProfile(memberId,
                memberProfileUpdateRequest);
        asyncExecutor.execute(() -> memberService.deleteMemberProfileImage(memberProfileUpdateResult));
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> registerMember(@AccessTokenValue final long memberId) {
        memberService.registerMember(memberId);
        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/me/mail-notification")
    public ResponseEntity<Void> updateIsConfirmedMailNotification(@AccessTokenValue final long memberId,
                                                                  @RequestParam("isConfirmed") final boolean isConfirmed) {
        memberService.updateConfirmedMailNotification(memberId, isConfirmed);
        return ResponseEntity.noContent()
                .build();
    }
}
