package com.tag.application.member;

import com.tag.application.image.ObjectStorageManager;
import com.tag.application.member.searchStrategy.MemberSearchStrategy;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.request.member.MemberDonationInfoUpdateRequest;
import com.tag.dto.request.member.MemberProfileUpdateRequest;
import com.tag.dto.response.member.MemberProfileUpdateResult;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import java.util.Objects;
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
    public MemberResponse findMember(final long memberId, final List<String> memberSearchCategories) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        final MemberResponse memberResponse = new MemberResponse();
        for (final MemberSearchStrategy memberSearchStrategy : memberSearchStrategies) {
            memberSearchStrategy.populateMemberResponse(memberSearchCategories, member, memberResponse);
        }
        return memberResponse;
    }

    @Transactional
    public void registerMember(final long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        member.register();
    }

    @Transactional
    public MemberProfileUpdateResult updateMemberProfile(final long memberId,
                                                         final MemberProfileUpdateRequest memberProfileUpdateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        final String introduction = memberProfileUpdateRequest.getIntroduction();
        member.updateIntroduction(introduction);
        final String profileImageName = memberProfileUpdateRequest.getProfileImageName();
        final String previousProfileImageName = member.getProfileImageName();
        if (Objects.equals(profileImageName, previousProfileImageName)) {
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
            objectStorageManager.deleteObject(previousProfileImageName);
        }
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
}
