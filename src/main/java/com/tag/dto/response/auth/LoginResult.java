package com.tag.dto.response.auth;

import com.tag.domain.member.Member;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginResult {

    private boolean isRegistered;
    private String accessToken;
    private String refreshToken;

    public LoginResult(final Member member, final String accessToken, final String refreshToken) {
        this.isRegistered = member.isRegistered();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
