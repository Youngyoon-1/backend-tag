package com.tag.dto.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class AccessTokenResponse {

    private String accessToken;
    private boolean registered;

    public AccessTokenResponse(final IssueAccessTokenResult issueAccessTokenResult) {
        this.accessToken = issueAccessTokenResult.getAccessToken();
        this.registered = issueAccessTokenResult.isRegistered();
    }

    private AccessTokenResponse() {
    }
}
