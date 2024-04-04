package com.tag.domain;

import lombok.Getter;

@Getter
public class RefreshToken {

    private final String refreshToken;
    private final Long memberId;

    public RefreshToken(final String refreshToken, final Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
