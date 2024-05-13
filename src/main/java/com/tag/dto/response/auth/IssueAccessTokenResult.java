package com.tag.dto.response.auth;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class IssueAccessTokenResult {

    private final String refreshToken;
    private final String accessToken;
    private final boolean registered;

    public IssueAccessTokenResult(final String refreshToken, final String accessToken, final boolean registered) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.registered = registered;
    }
}
