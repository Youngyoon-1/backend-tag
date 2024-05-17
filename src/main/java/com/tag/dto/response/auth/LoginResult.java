package com.tag.dto.response.auth;

import com.tag.domain.member.Member;

public record LoginResult(boolean isRegistered, String accessToken, String refreshToken) {

    public LoginResult(final Member member, final String accessToken, final String refreshToken) {
       this(member.isRegistered(), accessToken, refreshToken);
    }
}
