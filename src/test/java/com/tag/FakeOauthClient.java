package com.tag;

import com.tag.application.auth.OauthClient;
import com.tag.dto.response.auth.OauthProfileResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class FakeOauthClient implements OauthClient {

    @Override
    public OauthProfileResponse requestProfile(final String code) {
        return new OauthProfileResponse("test@test.com");
    }
}
