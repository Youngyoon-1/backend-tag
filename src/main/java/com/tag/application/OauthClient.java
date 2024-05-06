package com.tag.application;

import com.tag.dto.response.OauthProfileResponse;

public interface OauthClient {

    OauthProfileResponse getProfile(final String code);
}
