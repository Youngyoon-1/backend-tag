package com.tag.application;

import com.tag.domain.Member;
import com.tag.dto.request.MemberSearchCategory;
import com.tag.dto.response.MemberResponse;
import org.springframework.stereotype.Component;

@Component
public final class ProfileImageUrlSearchStrategy extends MemberSearchStrategy {

    private final ObjectStorageManager objectStorageManager;

    public ProfileImageUrlSearchStrategy(final ObjectStorageManager objectStorageManager) {
        this.objectStorageManager = objectStorageManager;
    }

    @Override
    boolean isApplicable(final String memberSearchCategory) {
        return MemberSearchCategory.hasProfileImageUrlFromParam(memberSearchCategory);
    }

    @Override
    void populateMemberResponse(final Member member, final MemberResponse memberResponse) {
        final String profileImageName = member.getProfileImageName();
        final String profileImageUrl = objectStorageManager.createGetUrl(profileImageName, MemberImageCategory.PROFILE);
        memberResponse.setProfileImageUrl(profileImageUrl);
    }
}
