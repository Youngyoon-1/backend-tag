package com.tag.dto.request.auth;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GoogleAccessTokenRequest(String code, String clientId, String clientSecret, String redirectUri,
                                       String grantType) {
}
