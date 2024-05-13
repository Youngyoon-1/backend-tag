package com.tag.dto.response.auth;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class LoginResponse {

    private boolean isRegistered;
    private String accessToken;

    private LoginResponse() {
    }

    public LoginResponse(final LoginResult loginResult) {
        this.isRegistered = loginResult.isRegistered();
        this.accessToken = loginResult.getAccessToken();
    }
}
