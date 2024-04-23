package com.tag.application;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.request.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.MemberProfileUpdateRequest;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberDonationInfoResponse;
import com.tag.dto.response.MemberImageGetUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberProfileUpdateResult;
import com.tag.dto.response.MemberResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ObjectStorageManager objectStorageManager;
    private final ImageService imageService;

    public MemberService(final MemberRepository memberRepository, final ObjectStorageManager objectStorageManager,
                         final ImageService imageService) {
        this.memberRepository = memberRepository;
        this.objectStorageManager = objectStorageManager;
        this.imageService = imageService;
    }

    @Transactional(readOnly = true)
    public MemberResponse findMember(final long memberId, final String memberSearchCategory) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
        // 회원의 정보, 이미지를 모두 제공
        if (memberSearchCategory == null) {
            final String profileImageName = member.getProfileImageName();
            final String profileImageUrl = getUrl(profileImageName, MemberImageCategory.PROFILE);
            return new MemberResponse(member, profileImageUrl);
        }
        final MemberResponse memberResponse = new MemberResponse();
        if (MemberSearchCategory.hasEmailFromParam(memberSearchCategory)) {
            final String email = member.getEmail();
            memberResponse.setEmail(email);
        }
        if (MemberSearchCategory.hasIntroductoryArticleFromParam(memberSearchCategory)) {
            final String introductoryArticle = member.getIntroductoryArticle();
            memberResponse.setIntroductoryArticle(introductoryArticle);
        }
        if (MemberSearchCategory.hasProfileImageUrlFromParam(memberSearchCategory)) {
            final String profileImageName = member.getProfileImageName();
            final String profileImageUrl = getUrl(profileImageName, MemberImageCategory.PROFILE);
            memberResponse.setProfileImageUrl(profileImageUrl);
        }
        if (MemberSearchCategory.hasQrImageUrlFromParam(memberSearchCategory)) {
            final String qrImageName = member.getQrImageName();
            final String qrImageUrl = getUrl(qrImageName, MemberImageCategory.QR);
            memberResponse.setQrImageUrl(qrImageUrl);
        }
        if (MemberSearchCategory.hasQrLinkUrlFromParam(memberSearchCategory)) {
            final String qrLinkUrl = member.getQrLinkUrl();
            memberResponse.setQrLinkUrl(qrLinkUrl);
        }
        return memberResponse;
    }

    private String getUrl(final String imageName, final MemberImageCategory memberImageCategory) {
        if (imageName != null) {
            return objectStorageManager.createPresignedGetUrl(imageName,
                    memberImageCategory);
        }
        return null;
    }

    @Transactional
    public MemberImageGetUrlResponse updateImageName(final Long memberId, final MemberImageCategory memberImageCategory,
                                                     final String imageName) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이기 때문에 이미지 이름을 변경할 수 없습니다."));
        if (memberImageCategory == MemberImageCategory.PROFILE) {
            member.updateProfileImageName(imageName);
            final String presignedGetUrl = objectStorageManager.createPresignedGetUrl(imageName,
                    MemberImageCategory.PROFILE);
            return new MemberImageGetUrlResponse(presignedGetUrl);
        }
        if (memberImageCategory == MemberImageCategory.QR) {
            member.updateQrImageName(imageName);
            final String presignedGetUrl = objectStorageManager.createPresignedGetUrl(imageName,
                    MemberImageCategory.QR);
            return new MemberImageGetUrlResponse(presignedGetUrl);
        }
        throw new RuntimeException("이미지 카테고리가 유효하지 않기 때문에 이미지 이름을 변경할 수 없습니다.");
    }

    @Transactional
    public MemberInfoUpdateResponse updateMemberInfo(final Long memberId, final MemberInfoCategory infoCategory,
                                                     final String content) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이기 때문에 이미지 이름을 변경할 수 없습니다."));
        if (infoCategory == MemberInfoCategory.INTRODUCTORY_ARTICLE) {
            member.updateIntroductoryArticle(content);
            return new MemberInfoUpdateResponse(content);
        }
        if (infoCategory == MemberInfoCategory.QR_LINK_URL) {
            member.updateQrLinkUrl(content);
            return new MemberInfoUpdateResponse(content);
        }
        throw new RuntimeException("회원정보 카테고리가 유효하지 않기 때문에 이미지 이름을 변경할 수 없습니다.");
    }

    @Transactional
    public void registerMember(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        member.register();
    }

    @Transactional(readOnly = true)
    public String getProfileImageUrl(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
        final String profileImageName = member.getProfileImageName();
        return getUrl(profileImageName, MemberImageCategory.PROFILE);
    }

    @Transactional
    public MemberProfileUpdateResult updateMemberProfile(final Long memberId,
                                                         final MemberProfileUpdateRequest memberProfileUpdateRequest) {
        final String introductoryArticle = memberProfileUpdateRequest.getIntroductoryArticle();
        if (introductoryArticle.length() > 500) {
            throw new RuntimeException("자기소개 글은 500자 이하여야 합니다.");
        }

        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
        member.updateIntroductoryArticle(introductoryArticle);
        final String profileImageName = memberProfileUpdateRequest.getProfileImageName();
        // nullable
        final String previousProfileImageName = member.getProfileImageName();
        if (previousProfileImageName == null && profileImageName.isEmpty()) {
            return MemberProfileUpdateResult.builder()
                    .profileImageUpdated(false)
                    .build();
        }
        if (profileImageName.equals(previousProfileImageName)) {
            return MemberProfileUpdateResult.builder()
                    .profileImageUpdated(false)
                    .build();
        }
        member.updateProfileImageName(profileImageName);
        return MemberProfileUpdateResult.builder()
                .profileImageUpdated(true)
                .previousProfileImageName(previousProfileImageName)
                .build();
    }

    public void deleteMemberProfileImage(final MemberProfileUpdateResult memberProfileUpdateResult) {
        final String previousProfileImageName = memberProfileUpdateResult.getPreviousProfileImageName();
        if (memberProfileUpdateResult.isProfileImageUpdated() && previousProfileImageName != null) {
            imageService.deleteObject(previousProfileImageName);
        }
    }

    @Transactional(readOnly = true)
    public boolean IsConfirmedMailNotification(final Long memberId) {
        return memberRepository.IsConfirmedMailNotification(memberId);
    }

    @Transactional
    public void updateConfirmedMailNotification(final Long memberId, final Boolean isConfirmed) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
        member.updateIsConfirmedMailNotification(isConfirmed);
    }

    @Transactional
    public void updateMemberDonationInfo(final Long memberId,
                                         final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest) {
        final String bankName = memberDonationInfoUpdateRequest.getBankName();
        final String accountNumber = memberDonationInfoUpdateRequest.getAccountNumber();
        final String accountHolder = memberDonationInfoUpdateRequest.getAccountHolder();

        if (!bankName.isEmpty() && !accountNumber.isEmpty() && !accountHolder.isEmpty()) {
            if (bankName.length() > 10 || bankName.length() < 2) {
                throw new RuntimeException("은행명이 유효하지 않습니다.");
            }
            if (accountNumber.length() > 15 || accountNumber.length() < 9) {
                throw new RuntimeException("계좌번호가 유효하지 않습니다.");
            }
            if (accountHolder.length() > 15 || accountHolder.length() < 2) {
                throw new RuntimeException("예금주가 유효하지 않습니다.");
            }
        }

        final String remitLink = memberDonationInfoUpdateRequest.getRemitLink();
        if (!remitLink.isEmpty()) {
            if (remitLink.length() > 100 || remitLink.length() < 10) {
                throw new RuntimeException("송금링크가 유효하지 않습니다.");
            }
        }

        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이기 때문에 후원정보를 변경할 수 없습니다."));
        member.updateDonationInfo(bankName, accountNumber, accountHolder, remitLink);
    }

    @Transactional(readOnly = true)
    public MemberDonationInfoResponse findDonationInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원이기 때문에 후원정보를 조회할 수 없습니다."));
        return new MemberDonationInfoResponse(member);
    }
}
