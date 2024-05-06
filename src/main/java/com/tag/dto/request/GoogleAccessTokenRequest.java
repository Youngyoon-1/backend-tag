package com.tag.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public final class GoogleAccessTokenRequest {

    private String code;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String grantType;

    public GoogleAccessTokenRequest(final String code, final String clientId, final String clientSecret,
                                    final String redirectUri,
                                    final String grantType) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
    }
}
