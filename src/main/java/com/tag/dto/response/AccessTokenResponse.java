package com.tag.dto.response;

import lombok.Getter;

@Getter
public class AccessTokenResponse {

    private String accessToken;
    private boolean registered;

    public AccessTokenResponse(final IssueAccessTokenResult issueAccessTokenResult) {
        this.accessToken = issueAccessTokenResult.getAccessToken();
        this.registered = issueAccessTokenResult.isRegistered();
    }

    private AccessTokenResponse() {
    }
}
