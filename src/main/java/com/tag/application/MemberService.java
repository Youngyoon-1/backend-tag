package com.tag.application;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.request.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.MemberProfileUpdateRequest;
import com.tag.dto.response.MemberDonationInfoResponse;
import com.tag.dto.response.MemberImageGetUrlResponse;
import com.tag.dto.response.MemberInfoUpdateResponse;
import com.tag.dto.response.MemberProfileUpdateResult;
import com.tag.dto.response.MemberResponse;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private static final String MEMBER_DOES_NOT_EXIST = "존재하지 않는 회원 입니다.";

    private final MemberRepository memberRepository;
    private final ObjectStorageManager objectStorageManager;
    private final List<MemberSearchStrategy> memberSearchStrategies;

    public MemberService(final MemberRepository memberRepository, final ObjectStorageManager objectStorageManager,
                         final List<MemberSearchStrategy> memberSearchStrategies) {
        this.memberRepository = memberRepository;
        this.objectStorageManager = objectStorageManager;
        this.memberSearchStrategies = memberSearchStrategies;
    }

    @Transactional(readOnly = true)
    // TODo: 변수이름, 변
    public MemberResponse findMember(final long memberId, final List<String> memberSearchCategories) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        if (memberSearchCategories == null || memberSearchCategories.isEmpty()) {
            final String profileImageName = member.getProfileImageName();
            final String profileImageUrl = objectStorageManager.createGetUrl(profileImageName,
                    MemberImageCategory.PROFILE);
            final String qrImageName = member.getQrImageName();
            final String qrImageUrl = objectStorageManager.createGetUrl(qrImageName, MemberImageCategory.QR);
            return new MemberResponse(member, profileImageUrl, qrImageUrl);
        }
        final MemberResponse memberResponse = new MemberResponse();
        for (final MemberSearchStrategy memberSearchStrategy : memberSearchStrategies) {
            memberSearchStrategy.populateMemberResponse(memberSearchCategories, member, memberResponse);
        }
        return memberResponse;
    }

    @Transactional
    public MemberImageGetUrlResponse updateImageName(final long memberId, final MemberImageCategory memberImageCategory,
                                                     final String imageName) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        if (memberImageCategory == MemberImageCategory.PROFILE) {
            member.updateProfileImageName(imageName);
            final String presignedGetUrl = objectStorageManager.createPresignedGetUrl(imageName,
                    MemberImageCategory.PROFILE);
            return new MemberImageGetUrlResponse(presignedGetUrl);
        }
        member.updateQrImageName(imageName);
        final String presignedGetUrl = objectStorageManager.createPresignedGetUrl(imageName,
                MemberImageCategory.QR);
        return new MemberImageGetUrlResponse(presignedGetUrl);
    }

    @Transactional
    public MemberInfoUpdateResponse updateMemberInfo(final long memberId, final MemberInfoCategory infoCategory,
                                                     final String content) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        if (infoCategory == MemberInfoCategory.INTRODUCTORY_ARTICLE) {
            member.updateIntroduction(content);
            return new MemberInfoUpdateResponse(content);
        }
        member.updateQrLinkUrl(content);
        return new MemberInfoUpdateResponse(content);
    }

    @Transactional
    public void registerMember(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        member.register();
    }

    @Transactional(readOnly = true)
    public String getProfileImageUrl(final long memberId) {
        final String profileImageName = memberRepository.findProfileImageNameById(memberId)
                .orElse(null);
        return objectStorageManager.createGetUrl(profileImageName, MemberImageCategory.PROFILE);
    }

    @Transactional
    public MemberProfileUpdateResult updateMemberProfile(final long memberId,
                                                         final MemberProfileUpdateRequest memberProfileUpdateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        final String introduction = memberProfileUpdateRequest.getIntroductoryArticle();
        // TODO: JPA dirty checking test
        member.updateIntroduction(introduction);
        final String profileImageName = memberProfileUpdateRequest.getProfileImageName();
        final String previousProfileImageName = member.getProfileImageName();
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

    @Async
    public void deleteMemberProfileImage(final MemberProfileUpdateResult memberProfileUpdateResult) {
        final String previousProfileImageName = memberProfileUpdateResult.getPreviousProfileImageName();
        if (memberProfileUpdateResult.isProfileImageUpdated() && previousProfileImageName != null) {
            objectStorageManager.deleteObject(previousProfileImageName);
        }
    }

    @Transactional(readOnly = true)
    public boolean IsConfirmedMailNotification(final long memberId) {
        return memberRepository.isConfirmedMailNotification(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
    }

    @Transactional
    public void updateConfirmedMailNotification(final long memberId, final boolean isConfirmed) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        member.updateIsConfirmedMailNotification(isConfirmed);
    }

    @Transactional
    public void updateMemberDonationInfo(final long memberId,
                                         final MemberDonationInfoUpdateRequest memberDonationInfoUpdateRequest) {
        final String bankName = memberDonationInfoUpdateRequest.getBankName();
        final String accountNumber = memberDonationInfoUpdateRequest.getAccountNumber();
        final String accountHolder = memberDonationInfoUpdateRequest.getAccountHolder();
        final String remitLink = memberDonationInfoUpdateRequest.getRemitLink();
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        member.updateDonationInfo(bankName, accountNumber, accountHolder, remitLink);
    }

    @Transactional(readOnly = true)
    public MemberDonationInfoResponse findDonationInfo(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        return new MemberDonationInfoResponse(member);
    }
}
