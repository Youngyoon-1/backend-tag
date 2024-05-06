package com.tag.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberProfileUpdateRequest {

    @Size(min = 1, max = 500)
    private String introductoryArticle;
    private String profileImageName;

    private MemberProfileUpdateRequest() {
    }

    public MemberProfileUpdateRequest(final String introductoryArticle, final String profileImageName) {
        this.introductoryArticle = introductoryArticle;
        this.profileImageName = profileImageName;
    }
}
