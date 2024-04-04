package com.tag.dto.request;

import lombok.Getter;

@Getter
public class MemberProfileUpdateRequest {

    private String introductoryArticle;
    private String profileImageName;

    private MemberProfileUpdateRequest() {
    }

    public MemberProfileUpdateRequest(final String introductoryArticle, final String profileImageName) {
        this.introductoryArticle = introductoryArticle;
        this.profileImageName = profileImageName;
    }
}
