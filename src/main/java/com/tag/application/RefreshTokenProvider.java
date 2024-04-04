package com.tag.application;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider {

    public String issueToken() {
        return UUID.randomUUID().toString();
    }
}
