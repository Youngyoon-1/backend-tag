package com.tag.dto.response;

import lombok.Getter;

@Getter
public class IssueAccessTokenResult {

    private final String refreshToken;
    private final String accessToken;
    private final boolean registered;

    public IssueAccessTokenResult(final String refreshToken, final String accessToken, final boolean registered) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.registered = registered;
    }
}
