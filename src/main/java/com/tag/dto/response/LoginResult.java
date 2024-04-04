package com.tag.dto.response;

import com.tag.domain.Member;
import lombok.Getter;

@Getter
public class LoginResult {

    private boolean isRegistered;
    private String email;
    private String introductoryArticle;
    private String profileImageUrl;
    private String qrImageUrl;
    private String qrLinkUrl;
    private String accessToken;
    private String refreshToken;

    public LoginResult(final Member member, final String profileImageUrl, final String qrImageUrl,
                       final String accessToken, final String refreshToken) {
        this.isRegistered = member.isRegistered();
        this.email = member.getEmail();
        this.introductoryArticle = member.getIntroductoryArticle();
        this.profileImageUrl = profileImageUrl;
        this.qrImageUrl = qrImageUrl;
        this.qrLinkUrl = member.getQrLinkUrl();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
