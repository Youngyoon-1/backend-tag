package com.tag.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.response.AccessTokenResponse;
import com.tag.dto.response.LoginResponse;
import java.net.HttpCookie;
import java.net.URI;
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

    @Autowired
    private MemberRepository memberRepository;

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
        final String email = loginResponse.getEmail();
        final String introductoryArticle = loginResponse.getIntroductoryArticle();
        final String profilePhotoUrl = loginResponse.getProfileImageUrl();
        final String qrPhotoUrl = loginResponse.getQrImageUrl();
        final String qrLinkUrl = loginResponse.getQrLinkUrl();
        final String accessToken = loginResponse.getAccessToken();
        Assertions.assertAll(
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(email).isNotNull(),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profilePhotoUrl).isNull(),
                () -> assertThat(qrPhotoUrl).isNull(),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> assertThat(accessToken).isNotNull()
        );
    }

    @Test
    void 최초_로그인을_한다2() {
        // when
        final ResponseEntity<LoginResponse> responseEntity = testRestTemplate.getForEntity(
                "/api/login",
                LoginResponse.class
        );

        // then
        final String cookieValue = responseEntity.getHeaders()
                .get("Set-cookie")
                .get(0);
        final LoginResponse loginResponse = responseEntity.getBody();
        final String email = loginResponse.getEmail();
        final String introductoryArticle = loginResponse.getIntroductoryArticle();
        final String profilePhotoUrl = loginResponse.getProfileImageUrl();
        final String qrPhotoUrl = loginResponse.getQrImageUrl();
        final String qrLinkUrl = loginResponse.getQrLinkUrl();
        final String accessToken = loginResponse.getAccessToken();
        Assertions.assertAll(
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(email).isNotNull(),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profilePhotoUrl).isNull(),
                () -> assertThat(qrPhotoUrl).isNull(),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> assertThat(accessToken).isNotNull()
        );
    }

    @Test
    void 회원정보_이미지가_등록된_회원이_로그인을_한다_() {
        // given
        final Member member = Member.builder()
                .id(10L)
                .email("test@test.com")
                .introduction("introductoryArticle")
                .profileImageName("profileImageName")
                .qrImageName("qrImageName")
                .qrLinkUrl("qrLinkUrl")
                .build();
        memberRepository.save(member);

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
        final String email = loginResponse.getEmail();
        final String introductoryArticle = loginResponse.getIntroductoryArticle();
        final String qrLinkUrl = loginResponse.getQrLinkUrl();
        final String accessToken = loginResponse.getAccessToken();
        final String profileImageUrl = loginResponse.getProfileImageUrl();
        final HttpStatusCode profileImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(profileImageUrl),
                        Void.class)
                .getStatusCode();
        final String qrImageUrl = loginResponse.getQrImageUrl();
        final HttpStatusCode qrImageResponseStatusCode = testRestTemplate.getForEntity(URI.create(qrImageUrl),
                        Void.class)
                .getStatusCode();
        Assertions.assertAll(
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(introductoryArticle).isEqualTo("introductoryArticle"),
                () -> assertThat(qrLinkUrl).isEqualTo("qrLinkUrl"),
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(profileImageResponseStatusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(qrImageResponseStatusCode).isEqualTo(HttpStatus.OK)
        );
    }

//    @Test
//    void 로그인을_실패한다_구글_엑세스_토큰_발급에_실패한_경우() {
//        // when
//        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.getForEntity(
//                "/api/login?code=invalidCode",
//                ExceptionResponse.class
//        );
//
//        // then
//        final HttpStatusCode statusCode = responseEntity.getStatusCode();
//        final String message = responseEntity.getBody()
//                .getMessage();
//        Assertions.assertAll(
//                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
//                () -> assertThat(message).isEqualTo("구글 액세스 토큰 발급 과정에서 예외가 발생했습니다.")
//        );
//    }

//    @Test
//    void 로그인을_실패한다_구글_프로필_조회에_실패한_경우() {
//        // when
//        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.getForEntity(
//                "/api/login?code=issueInvalidAccessToken",
//                ExceptionResponse.class
//        );
//
//        // then
//        final HttpStatusCode statusCode = responseEntity.getStatusCode();
//        final String message = responseEntity.getBody()
//                .getMessage();
//        Assertions.assertAll(
//                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
//                () -> assertThat(message).isEqualTo("구글 프로필 조회 과정에서 예외가 발생했습니다")
//        );
//    }

//    @Test
//    void 로그인을_실패한다_구글_인증_서버에_문제가_발생한_경우() {
//        // when
//        final ResponseEntity<ExceptionResponse> responseEntity = testRestTemplate.getForEntity(
//                "/api/login?code=willReturn500Code",
//                ExceptionResponse.class
//        );
//
//        // then
//        final HttpStatusCode statusCode = responseEntity.getStatusCode();
//        final String message = responseEntity.getBody()
//                .getMessage();
//        Assertions.assertAll(
//                () -> assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST),
//                () -> assertThat(message).isEqualTo("구글 인증 서버에 문제가 발생했습니다.")
//        );
//    }

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
                .getAccessToken();
        Assertions.assertAll(
                () -> assertThat(statusCode).isEqualTo(HttpStatus.OK),
                () -> assertThat(cookieValue).contains("refreshToken"),
                () -> assertThat(accessToken).isNotNull()
        );
    }
}
