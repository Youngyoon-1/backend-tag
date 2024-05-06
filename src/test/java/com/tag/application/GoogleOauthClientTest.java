package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.tag.dto.request.GoogleAccessTokenRequest;
import com.tag.dto.response.GoogleAccessTokenResponse;
import com.tag.dto.response.OauthProfileResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GoogleOauthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleOauthClient googleOauthClient;

    @BeforeEach
    void setUp() {
        googleOauthClient = new GoogleOauthClient(
                "test_client_id",
                "test_client_secret",
                restTemplate
        );
    }

    @Test
    void 구글_프로필을_조회한다() {
        // given
        final ResponseEntity<GoogleAccessTokenResponse> accessTokenResponse = new ResponseEntity<>(
                new GoogleAccessTokenResponse("accessToken"),
                HttpStatus.OK
        );
        BDDMockito.given(restTemplate.postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)))
                .willReturn(accessTokenResponse);
        final ResponseEntity<OauthProfileResponse> profileResponse = new ResponseEntity<>(
                new OauthProfileResponse("test@test.com"),
                HttpStatus.OK
        );
        BDDMockito.given(restTemplate.exchange(
                        eq("http://localhost:8888/profile"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(OauthProfileResponse.class)))
                .willReturn(profileResponse);

        // when
        final OauthProfileResponse oauthProfileResponse = googleOauthClient.getProfile("testCode");

        // then
        final String email = oauthProfileResponse.getEmail();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> BDDMockito.verify(restTemplate).postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)),
                () -> BDDMockito.verify(restTemplate).exchange(
                        eq("http://localhost:8888/profile"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(OauthProfileResponse.class))
        );
    }

    @Test
    void 구글_프로필을_조회한다_구글_엑세스_토큰_발급에_실패한_경우() {
        // given
        BDDMockito.given(restTemplate.postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> googleOauthClient.getProfile("testCode")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("구글 액세스 토큰 발급 과정에서 예외가 발생했습니다."),
                () -> BDDMockito.verify(restTemplate).postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class))
        );
    }

    @Test
    void 구글_프로필을_조회한다_구글_프로필_조회에_실패한_경우() {
        // given
        final ResponseEntity<GoogleAccessTokenResponse> accessTokenResponse = new ResponseEntity<>(
                new GoogleAccessTokenResponse("accessToken"),
                HttpStatus.OK
        );
        BDDMockito.given(restTemplate.postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)))
                .willReturn(accessTokenResponse);
        BDDMockito.given(restTemplate.exchange(
                        eq("http://localhost:8888/profile"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(OauthProfileResponse.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> googleOauthClient.getProfile("testCode")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("구글 프로필 조회 과정에서 예외가 발생했습니다"),
                () -> BDDMockito.verify(restTemplate).postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)),
                () -> BDDMockito.verify(restTemplate).exchange(
                        eq("http://localhost:8888/profile"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(OauthProfileResponse.class))
        );
    }

    @Test
    void 구글_프로필을_조회한다_엑세스_토큰에_실패한_경우_예외가_발생한다() {
        // given
        final ResponseEntity<GoogleAccessTokenResponse> accessTokenResponse = new ResponseEntity<>(
                HttpStatus.BAD_REQUEST
        );
        BDDMockito.given(restTemplate.postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)))
                .willReturn(accessTokenResponse);

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> googleOauthClient.getProfile("testCode")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("구글 액세스 토큰 발급 과정에서 예외가 발생했습니다."),
                () -> BDDMockito.verify(restTemplate).postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class))
        );
    }

    @Test
    void 구글_프로필을_조회한다_구글_인증_서버에_문제가_발생한_경우_예외가_발생한다() {
        // given
        BDDMockito.given(restTemplate.postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class)))
                .willThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> googleOauthClient.getProfile("testCode")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("구글 인증 서버에 문제가 발생했습니다."),
                () -> BDDMockito.verify(restTemplate).postForEntity(
                        eq("http://localhost:8888/token"),
                        any(GoogleAccessTokenRequest.class),
                        eq(GoogleAccessTokenResponse.class))
        );
    }
}
