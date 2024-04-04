package com.tag.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {

    private boolean isRegistered;
    private String email;
    private String introductoryArticle;
    private String profileImageUrl;
    private String qrImageUrl;
    private String qrLinkUrl;
    private String accessToken;

    private LoginResponse() {
    }

    public LoginResponse(final LoginResult loginResult) {
        this.isRegistered = loginResult.isRegistered();
        this.email = loginResult.getEmail();
        this.introductoryArticle = loginResult.getIntroductoryArticle();
        this.profileImageUrl = loginResult.getProfileImageUrl();
        this.qrImageUrl = loginResult.getQrImageUrl();
        this.qrLinkUrl = loginResult.getQrLinkUrl();
        this.accessToken = loginResult.getAccessToken();
    }
}
