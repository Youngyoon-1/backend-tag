package com.tag.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RefreshTokenProviderTest {

    @Test
    void 리프레시_토큰을_생성한다() {
        // given
        final RefreshTokenProvider refreshTokenProvider = new RefreshTokenProvider();

        // when
        final String refreshToken = refreshTokenProvider.issueToken();

        // then
        Assertions.assertThat(refreshToken).isNotNull();
    }
}
