package com.tag.application;

import com.tag.dto.request.GoogleAccessTokenRequest;
import com.tag.dto.response.GoogleAccessTokenResponse;
import com.tag.dto.response.GoogleProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOauthClient {

    private final String accessTokenRequestUrl;
    private final String userProfileRequestUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private static final String GRANT_TYPE = "authorization_code";
    private final RestTemplate restTemplate;

    public GoogleOauthClient(@Value("${google.api-url.access-token}") final String accessTokenRequestUrl,
                             @Value("${google.api-url.profile}") final String userProfileRequestUrl,
                             @Value("${google.oauth.client-id}") final String clientId,
                             @Value("${google.oauth.redirect-uri}") final String redirectUri,
                             @Value("${google.oauth.client-secret}") final String clientSecret,
                             final RestTemplate restTemplate) {
        this.accessTokenRequestUrl = accessTokenRequestUrl;
        this.userProfileRequestUrl = userProfileRequestUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.restTemplate = restTemplate;
    }

    public GoogleProfileResponse requestProfile(final String code) {
        final GoogleAccessTokenRequest googleAccessTokenRequest = new GoogleAccessTokenRequest(code, clientId,
                clientSecret, redirectUri, GRANT_TYPE);
        final ResponseEntity<GoogleAccessTokenResponse> googleAccessTokenResponse = requestAccessToken(
                googleAccessTokenRequest);
        if (!googleAccessTokenResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 액세스 토큰 발급 과정에서 예외가 발생했습니다.");
        }
        final String googleAccessToken = googleAccessTokenResponse.getBody()
                .getAccessToken();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);
        final HttpEntity httpEntity = new HttpEntity(headers);
        final ResponseEntity<GoogleProfileResponse> googleProfileResponse = requestProfile(httpEntity);
        if (!googleProfileResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 프로필 조회 과정에서 예외가 발생했습니다");
        }
        return googleProfileResponse.getBody();
    }

    private ResponseEntity<GoogleAccessTokenResponse> requestAccessToken(
            final GoogleAccessTokenRequest googleAccessTokenRequest) {
        try {
            return restTemplate.postForEntity(
                    accessTokenRequestUrl, googleAccessTokenRequest, GoogleAccessTokenResponse.class);
        } catch (final HttpClientErrorException e) {
            throw new RuntimeException("구글 액세스 토큰 발급 과정에서 예외가 발생했습니다.");
        } catch (final HttpServerErrorException e) {
            throw new RuntimeException("구글 인증 서버에 문제가 발생했습니다.");
        }
    }

    private ResponseEntity<GoogleProfileResponse> requestProfile(final HttpEntity httpEntity) {
        try {
            return restTemplate.exchange(userProfileRequestUrl,
                    HttpMethod.GET, httpEntity, GoogleProfileResponse.class);
        } catch (final HttpClientErrorException e) {
            throw new RuntimeException("구글 프로필 조회 과정에서 예외가 발생했습니다");
        }
    }
}
