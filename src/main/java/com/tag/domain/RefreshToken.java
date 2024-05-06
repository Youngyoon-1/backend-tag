package com.tag.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class RefreshToken {

    private final String refreshToken;
    private final long memberId;

    public RefreshToken(final String refreshToken, final long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
