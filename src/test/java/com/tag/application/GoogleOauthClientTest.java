package com.tag.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.tag.application.auth.GoogleOauthClient;
import com.tag.dto.request.auth.GoogleAccessTokenRequest;
import com.tag.dto.response.auth.GoogleAccessTokenResponse;
import com.tag.dto.response.auth.OauthProfileResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class GoogleOauthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private GoogleOauthClient googleOauthClient;

    @BeforeEach
    void setUp() {
        googleOauthClient = new GoogleOauthClient("id", "secret", restTemplate);
    }

    @Test
    void 프로필을_조회한다() {
        // given
        final GoogleAccessTokenResponse googleAccessTokenResponse = new GoogleAccessTokenResponse("accessToken");
        BDDMockito.given(restTemplate.postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class)))
                .willReturn(ResponseEntity.of(Optional.of(googleAccessTokenResponse)));
        final OauthProfileResponse oauthProfileResponse = new OauthProfileResponse("test@test.com");
        BDDMockito.given(restTemplate.exchange(eq("https://www.googleapis.com/oauth2/v1/userinfo"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(org.springframework.http.HttpEntity.class), eq(OauthProfileResponse.class)))
                .willReturn(ResponseEntity.of(Optional.of(oauthProfileResponse)));

        // when
        googleOauthClient.requestProfile("test");

        // then
        assertAll(
                () -> verify(restTemplate).postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class)),
                () -> verify(restTemplate).exchange(eq("https://www.googleapis.com/oauth2/v1/userinfo"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(org.springframework.http.HttpEntity.class), eq(OauthProfileResponse.class))
        );
    }

    @Test
    void 프로필_조회시_엑세스토큰을_발급받지_못하면_예외가_발생한다() {
        // given
        BDDMockito.given(restTemplate.postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class)))
                .willReturn(ResponseEntity.of(Optional.empty()));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> googleOauthClient.requestProfile("test"))
                        .isInstanceOf(NullPointerException.class),
                () -> verify(restTemplate).postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class))
        );
    }

    @Test
    void 프로필_조회가_실패하면_예외가_발생한다() {
        // given
        final GoogleAccessTokenResponse googleAccessTokenResponse = new GoogleAccessTokenResponse("accessToken");
        BDDMockito.given(restTemplate.postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class)))
                .willReturn(ResponseEntity.of(Optional.of(googleAccessTokenResponse)));
        BDDMockito.given(restTemplate.exchange(eq("https://www.googleapis.com/oauth2/v1/userinfo"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(org.springframework.http.HttpEntity.class), eq(OauthProfileResponse.class)))
                .willReturn(ResponseEntity.of(Optional.empty()));

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> googleOauthClient.requestProfile("test"))
                        .isInstanceOf(NullPointerException.class),
                () -> verify(restTemplate).postForEntity(eq("https://oauth2.googleapis.com/token"), any(
                        GoogleAccessTokenRequest.class), eq(GoogleAccessTokenResponse.class)),
                () -> verify(restTemplate).exchange(eq("https://www.googleapis.com/oauth2/v1/userinfo"),
                        eq(org.springframework.http.HttpMethod.GET),
                        any(org.springframework.http.HttpEntity.class), eq(OauthProfileResponse.class))
        );
    }
}
