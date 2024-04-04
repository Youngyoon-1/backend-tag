package com.tag.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthTokenExtractorTest {

    private final AuthTokenExtractor authTokenExtractor = new AuthTokenExtractor();

    @Test
    void http_authorization_header_에_있는_토큰_값을_추출한다() {
        // when
        final String httpAuthorizationHeader = "tokenType tokenValue";
        final String token = authTokenExtractor.extractToken(httpAuthorizationHeader, "tokenType");

        // then
        Assertions.assertThat(token).isEqualTo("tokenValue");
    }

    @Test
    void http_authorization_header_에_있는_토큰_값을_추출한다_http_authorization_header_가_null_인_경우_예외가_발생한다() {
        Assertions.assertThatThrownBy(
                        () -> authTokenExtractor.extractToken(null, "tokenType")
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 존재하지 않습니다.");
    }

    @Test
    void http_authorization_header_에_있는_토큰_값을_추출한다_토큰_형식이_type_value_형식이_아닌_경우_예외가_발생한다() {
        final String httpAuthorizationHeader = "tokenType tokenValue1 tokenValue2";
        Assertions.assertThatThrownBy(
                        () -> authTokenExtractor.extractToken(httpAuthorizationHeader, "tokenType")
                ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰의 형식이 유효하지 않습니다.");
    }
}
