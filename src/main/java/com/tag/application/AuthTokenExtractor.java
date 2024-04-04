package com.tag.application;

import org.springframework.stereotype.Component;

@Component
public class AuthTokenExtractor {

    public String extractToken(final String authorizationHeader, final String tokenType) {
        if (authorizationHeader == null) {
            throw new RuntimeException("토큰이 존재하지 않습니다.");
        }
        final String[] splitHeaders = authorizationHeader.split(" ");
        if (splitHeaders.length != 2 || !splitHeaders[0].equalsIgnoreCase(tokenType)) {
            throw new RuntimeException("토큰의 형식이 유효하지 않습니다.");
        }
        return splitHeaders[1];
    }
}
