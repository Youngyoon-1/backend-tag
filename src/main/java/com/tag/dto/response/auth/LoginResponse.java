package com.tag.dto.response.auth;

public record LoginResponse(boolean isRegistered, String accessToken) {
    public LoginResponse(final LoginResult loginResult) {
        this(loginResult.isRegistered(), loginResult.accessToken());
    }
}
