package com.tag.application.auth;

import com.tag.dto.response.auth.OauthProfileResponse;

public interface OauthClient {

    OauthProfileResponse requestProfile(final String code);
}
