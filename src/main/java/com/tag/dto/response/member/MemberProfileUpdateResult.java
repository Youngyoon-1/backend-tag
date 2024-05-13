package com.tag.dto.response.member;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public final class MemberProfileUpdateResult {

    private final boolean profileImageUpdated;
    private final String previousProfileImageName;

    public MemberProfileUpdateResult(final boolean profileImageUpdated, final String previousProfileImageName) {
        this.profileImageUpdated = profileImageUpdated;
        this.previousProfileImageName = previousProfileImageName;
    }
}
