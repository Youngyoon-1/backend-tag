package com.tag.application.member.searchStrategy;

import com.tag.domain.member.Member;
import com.tag.dto.request.member.MemberSearchCategory;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ProfileImageNameSearchStrategy extends MemberSearchStrategy {

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        return memberSearchCategories.stream()
                .anyMatch(MemberSearchCategory.VIEW_PROFILE_IMAGE_NAME.value::equalsIgnoreCase);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String profileImageName = member.getProfileImageName();
        memberResponse.setProfileImageName(profileImageName);
    }
}
