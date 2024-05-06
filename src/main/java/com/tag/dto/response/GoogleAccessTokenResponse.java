package com.tag.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@ToString
public final class GoogleAccessTokenResponse {

    private String accessToken;

    private GoogleAccessTokenResponse() {
    }

    public GoogleAccessTokenResponse(final String accessToken) {
        this.accessToken = accessToken;
    }
}

