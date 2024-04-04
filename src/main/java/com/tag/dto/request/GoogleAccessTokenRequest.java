package com.tag.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleAccessTokenRequest {

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
