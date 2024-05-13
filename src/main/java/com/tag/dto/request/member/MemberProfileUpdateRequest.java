package com.tag.dto.request.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberProfileUpdateRequest {

    @Size(min = 1, max = 500)
    private String introduction;
    private String profileImageName;

    private MemberProfileUpdateRequest() {
    }

    public MemberProfileUpdateRequest(final String introduction, final String profileImageName) {
        this.introduction = introduction;
        this.profileImageName = profileImageName;
    }
}
