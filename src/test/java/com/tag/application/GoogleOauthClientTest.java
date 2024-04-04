package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.tag.dto.request.GoogleAccessTokenRequest;
import com.tag.dto.response.GoogleAccessTokenResponse;
import com.tag.dto.response.GoogleProfileResponse;
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
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GoogleOauthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleOauthClient googleOauthClient;

    @BeforeEach
    void setUp() {
        this.googleOauthClient = new GoogleOauthClient(
                "http://localhost:8888/token",
                "http://localhost:8888/profile",
                "test_client_id",
                "http://localhost:8888/api/login",
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
        final ResponseEntity<GoogleProfileResponse> profileResponse = new ResponseEntity<>(
                new GoogleProfileResponse( "test@test.com"),
                HttpStatus.OK
        );
        BDDMockito.given(restTemplate.exchange(
                        eq("http://localhost:8888/profile"),
                        eq(HttpMethod.GET),
                        any(HttpEntity.class),
                        eq(GoogleProfileResponse.class)))
                .willReturn(profileResponse);

        // when
        final GoogleProfileResponse googleProfileResponse = googleOauthClient.requestProfile("testCode");

        // then
        final String email = googleProfileResponse.getEmail();
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
                        eq(GoogleProfileResponse.class))
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
                        () -> googleOauthClient.requestProfile("testCode")
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
                        eq(GoogleProfileResponse.class)))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> googleOauthClient.requestProfile("testCode")
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
                        eq(GoogleProfileResponse.class))
        );
    }
}
