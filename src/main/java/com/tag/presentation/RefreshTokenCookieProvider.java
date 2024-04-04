package com.tag.presentation;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieProvider {

    protected static final String REFRESH_TOKEN = "refreshToken";

    private final long expireLength;

    public RefreshTokenCookieProvider(@Value("${refresh-token.expire-length}") final long expireLength) {
        this.expireLength = expireLength;
    }

    public ResponseCookie createCookie(final String cookieValue) {
        return createCookie(cookieValue, Duration.ofMillis(expireLength));
    }

    private ResponseCookie createCookie(final String cookieValue, final Duration duration) {
        return ResponseCookie.from(REFRESH_TOKEN, cookieValue)
                .maxAge(duration)
//                .httpOnly(true)
//                .secure(true)
                .path("/api")
//                .sameSite(SameSite.NONE.attributeValue())
                .build();
    }

    public ResponseCookie createLogoutCookie() {
        return createCookie("", Duration.ZERO);
    }
}
