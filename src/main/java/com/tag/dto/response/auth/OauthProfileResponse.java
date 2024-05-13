package com.tag.dto.response.auth;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class OauthProfileResponse {

    private String email;

    private OauthProfileResponse() {
    }

    public OauthProfileResponse(final String email) {
        this.email = email;
    }
}
