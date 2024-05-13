package com.tag.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.presentation.auth.RefreshTokenCookieProvider;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;

class RefreshTokenCookieProviderTest {

    private static final RefreshTokenCookieProvider refreshTokenCookieProvider = new RefreshTokenCookieProvider(1000);

    @Test
    void 리프레시_토큰이_들어있는_쿠키를_생성한다() {
        // when
        final ResponseCookie cookie = refreshTokenCookieProvider.createCookie("refresh_token");

        // then
        final Duration maxAge = cookie.getMaxAge();
        final String name = cookie.getName();
        final String value = cookie.getValue();
        final boolean httpOnly = cookie.isHttpOnly();
        final boolean secure = cookie.isSecure();
        final String path = cookie.getPath();
        final String sameSite = cookie.getSameSite();
        Assertions.assertAll(
                () -> assertThat(maxAge).isEqualTo(Duration.ofSeconds(1L)),
                () -> assertThat(name).isEqualTo("refreshToken"),
                () -> assertThat(value).isEqualTo("refresh_token"),
                () -> assertThat(httpOnly).isTrue(),
                () -> assertThat(secure).isTrue(),
                () -> assertThat(path).isEqualTo("/api"),
                () -> assertThat(sameSite).isEqualTo(SameSite.NONE.attributeValue())
        );
    }

    @Test
    void 로그아웃_쿠키를_생성한다() {
        // when
        final ResponseCookie cookie = refreshTokenCookieProvider.createLogoutCookie();

        // then
        final String name = cookie.getName();
        final String value = cookie.getValue();
        final Duration maxAge = cookie.getMaxAge();
        final boolean httpOnly = cookie.isHttpOnly();
        final boolean secure = cookie.isSecure();
        final String path = cookie.getPath();
        final String sameSite = cookie.getSameSite();
        Assertions.assertAll(
                () -> assertThat(maxAge).isEqualTo(Duration.ZERO),
                () -> assertThat(name).isEqualTo("refreshToken"),
                () -> assertThat(value).isEmpty(),
                () -> assertThat(httpOnly).isTrue(),
                () -> assertThat(secure).isTrue(),
                () -> assertThat(path).isEqualTo("/api"),
                () -> assertThat(sameSite).isEqualTo(SameSite.NONE.attributeValue())
        );
    }
}
