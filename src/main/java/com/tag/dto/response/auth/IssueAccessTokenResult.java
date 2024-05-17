package com.tag.dto.response.auth;

public record IssueAccessTokenResult(String refreshToken, String accessToken, boolean isRegistered) {
}
