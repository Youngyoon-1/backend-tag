package com.tag.application.auth;

import com.tag.dto.request.auth.GoogleAccessTokenRequest;
import com.tag.dto.response.auth.GoogleAccessTokenResponse;
import com.tag.dto.response.auth.OauthProfileResponse;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("prod")
@Component
public final class GoogleOauthClient implements OauthClient {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String REDIRECT_URL = "postmessage";
    private static final String ACCESS_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
    private static final String PROFILE_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

    private final String clientId;
    private final String clientSecret;
    private final RestTemplate restTemplate;

    public GoogleOauthClient(@Value("${google.oauth.client-id}") final String clientId,
                             @Value("${google.oauth.client-secret}") final String clientSecret,
                             final RestTemplate restTemplate) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.restTemplate = restTemplate;
    }

    @Override
    public OauthProfileResponse requestProfile(final String code) {
        final String googleAccessToken = requestAccessToken(code);
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);
        final HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(headers);
        final ResponseEntity<OauthProfileResponse> googleProfileResponse = restTemplate.exchange(PROFILE_REQUEST_URL,
                HttpMethod.GET, httpEntity, OauthProfileResponse.class);
        return Objects.requireNonNull(googleProfileResponse.getBody());
    }

    private String requestAccessToken(final String code) {
        final GoogleAccessTokenRequest googleAccessTokenRequest = new GoogleAccessTokenRequest(code, clientId,
                clientSecret, REDIRECT_URL, GRANT_TYPE);
        final ResponseEntity<GoogleAccessTokenResponse> googleAccessTokenResponse = restTemplate.postForEntity(
                ACCESS_TOKEN_REQUEST_URL, googleAccessTokenRequest, GoogleAccessTokenResponse.class);
        return Objects.requireNonNull(googleAccessTokenResponse.getBody())
                .getAccessToken();
    }
}
