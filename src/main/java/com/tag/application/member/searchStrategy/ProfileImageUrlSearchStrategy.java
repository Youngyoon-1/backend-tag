package com.tag.application.member.searchStrategy;

import com.tag.application.image.ObjectStorageManager;
import com.tag.domain.member.Member;
import com.tag.dto.request.member.MemberSearchCategory;
import com.tag.dto.response.member.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class ProfileImageUrlSearchStrategy extends MemberSearchStrategy {

    private final ObjectStorageManager objectStorageManager;

    public ProfileImageUrlSearchStrategy(final ObjectStorageManager objectStorageManager) {
        this.objectStorageManager = objectStorageManager;
    }

    @Override
    boolean isApplicable(final List<String> memberSearchCategories) {
        return memberSearchCategories.stream()
                .anyMatch(MemberSearchCategory.VIEW_PROFILE_IMAGE_URL.value::equalsIgnoreCase);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String profileImageName = member.getProfileImageName();
        final String profileImageUrl = objectStorageManager.createGetUrl(profileImageName);
        memberResponse.setProfileImageUrl(profileImageUrl);
    }
}
