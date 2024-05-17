package com.tag;

import com.tag.dto.request.auth.GoogleAccessTokenRequest;
import com.tag.dto.response.auth.OauthProfileResponse;
import com.tag.dto.response.auth.GoogleAccessTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FakeOauthController {

    @PostMapping("/token")
    public ResponseEntity<GoogleAccessTokenResponse> issueAccessToken(
            @RequestBody final GoogleAccessTokenRequest googleAccessTokenRequest) {
        if (null == googleAccessTokenRequest.clientId() ||
                null == googleAccessTokenRequest.clientSecret() ||
                null == googleAccessTokenRequest.code() ||
                "invalidCode".equals(googleAccessTokenRequest.code()) ||
                null == googleAccessTokenRequest.grantType()) {
            return ResponseEntity.badRequest()
                    .build();
        }
        if (googleAccessTokenRequest.code().equals("willReturn500Code")) {
            return ResponseEntity.internalServerError()
                    .build();
        }
        if ("issueInvalidAccessToken".equals(googleAccessTokenRequest.code())) {
            final GoogleAccessTokenResponse googleAccessTokenResponse = new GoogleAccessTokenResponse(
                    "invalidAccessToken");
            return ResponseEntity.ok(googleAccessTokenResponse);
        }
        final GoogleAccessTokenResponse googleAccessTokenResponse = new GoogleAccessTokenResponse("fake_access_token");
        return ResponseEntity.ok(googleAccessTokenResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<OauthProfileResponse> showProfile(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) final String authorizationHeaderValue) {
        if (null == authorizationHeaderValue ||
                authorizationHeaderValue.contains("invalidAccessToken")) {
            return ResponseEntity.badRequest()
                    .build();
        }
        final OauthProfileResponse oauthProfileResponse = new OauthProfileResponse("test@test.com");
        return ResponseEntity.ok(oauthProfileResponse);
    }
}
