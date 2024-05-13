package com.tag.application.member.searchStrategy;

import com.tag.domain.member.DonationInfo;
import com.tag.domain.member.Member;
import com.tag.dto.request.member.MemberSearchCategory;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class DonationInfoSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        return memberSearchCategories.stream()
                .anyMatch(MemberSearchCategory.VIEW_DONATION_INFO.value::equalsIgnoreCase);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final DonationInfo donationInfo = member.getDonationInfo();
        memberResponse.setDonationInfo(donationInfo);
    }
}
