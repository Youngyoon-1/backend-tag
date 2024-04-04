package com.tag.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleAccessTokenResponse {

    private String accessToken;

    private GoogleAccessTokenResponse() {
    }

    public GoogleAccessTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}

