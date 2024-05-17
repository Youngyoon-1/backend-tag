package com.tag.dto.response.auth;

public record AccessTokenResponse(String accessToken, boolean registered) {
    public AccessTokenResponse(final IssueAccessTokenResult issueAccessTokenResult) {
        this(issueAccessTokenResult.accessToken(), issueAccessTokenResult.registered());
    }
}
