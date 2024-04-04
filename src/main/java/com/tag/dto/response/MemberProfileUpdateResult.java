package com.tag.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileUpdateResult {

    private final boolean profileImageUpdated;
    private String previousProfileImageName;

    public MemberProfileUpdateResult(final boolean profileImageUpdated, final String previousProfileImageName) {
        this.profileImageUpdated = profileImageUpdated;
        this.previousProfileImageName = previousProfileImageName;
    }
}
