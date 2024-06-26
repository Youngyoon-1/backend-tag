package com.tag.presentation.auth;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public final class RefreshTokenCookieProvider {

    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String LOGOUT_COOKIE_VALUE = "";
    private static final String COOKIE_PATH = "/api";

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
                .httpOnly(true)
                .secure(true)
                .path(COOKIE_PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();
    }

    public ResponseCookie createLogoutCookie() {
        return createCookie(LOGOUT_COOKIE_VALUE, Duration.ZERO);
    }
}
