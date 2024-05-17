package com.tag.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.dto.response.auth.AccessTokenResponse;
import com.tag.dto.response.auth.LoginResponse;
import java.net.HttpCookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@AcceptanceTest
public class AuthAcceptanceTest extends WithTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void 최초_로그인을_한다() {
        // when
        final ResponseEntity<LoginResponse> responseEntity = testRestTemplate.getForEntity(
                "/api/login?code=test",
                LoginResponse.class
        );

        // then
        final String cookieValue = responseEntity.getHeaders()
                .get("Set-cookie")
                .get(0);
        final LoginResponse loginResponse = responseEntity.getBody();
        final boolean isRegistered = loginResponse.isRegistered();
        final String accessToken = loginResponse.accessToken();
        Assertions.assertAll(
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(isRegistered).isFalse(),
                () -> assertThat(accessToken).isNotNull()
        );
    }

    @Test
    void 로그아웃을_한다() {
        // given
        final ResponseEntity<LoginResponse> responseEntity = testRestTemplate.getForEntity(
                "/api/login?code=test",
                null,
                LoginResponse.class
        );
        final String cookie = responseEntity.getHeaders()
                .get("Set-Cookie")
                .get(0);

        // when
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        final HttpEntity httpEntity = new HttpEntity<>(headers);
        final ResponseEntity<Void> response = testRestTemplate.exchange(
                "/api/logout",
                HttpMethod.DELETE, httpEntity,
                Void.class
        );

        // then
        final HttpStatusCode statusCode = response.getStatusCode();
        final String expiredCookie = response.getHeaders()
                .get(HttpHeaders.SET_COOKIE)
                .get(0);
        final HttpCookie logoutCookie = HttpCookie.parse(expiredCookie)
                .get(0);
        final String name = logoutCookie.getName();
        final String value = logoutCookie.getValue();
        final long maxAge = logoutCookie.getMaxAge();
        final boolean httpOnly = logoutCookie.isHttpOnly();
        final boolean secure = logoutCookie.getSecure();
        final String path = logoutCookie.getPath();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT),
                () -> assertThat(name).isEqualTo("refreshToken"),
                () -> assertThat(value).isEmpty(),
                () -> assertThat(maxAge).isZero(),
                () -> assertThat(httpOnly).isTrue(),
                () -> assertThat(secure).isTrue(),
                () -> assertThat(path).isEqualTo("/api")
        );
    }

    @Test
    void 로그인을_하고_엑세스_토큰을_발급받는다() {
        // given
        final ResponseEntity<LoginResponse> responseEntity = testRestTemplate.getForEntity(
                "/api/login?code=test",
                null,
                LoginResponse.class
        );
        final String cookie = responseEntity.getHeaders()
                .get("Set-Cookie")
                .get(0);

        // when
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, cookie);
        final HttpEntity httpEntity = new HttpEntity<>(headers);
        final ResponseEntity<AccessTokenResponse> response = testRestTemplate.postForEntity(
                "/api/token",
                httpEntity,
                AccessTokenResponse.class
        );

        // then
        final HttpStatusCode statusCode = response.getStatusCode();
        final String cookieValue = response.getHeaders()
                .get(HttpHeaders.SET_COOKIE)
                .get(0);
        final String accessToken = response.getBody()
                .accessToken();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(accessToken).isNotNull()
        );
    }
}
