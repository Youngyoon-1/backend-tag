package com.tag.dto.response.auth;

public record AccessTokenResponse(String accessToken, boolean isRegistered) {
    public AccessTokenResponse(final IssueAccessTokenResult issueAccessTokenResult) {
        this(issueAccessTokenResult.accessToken(), issueAccessTokenResult.isRegistered());
    }
}
