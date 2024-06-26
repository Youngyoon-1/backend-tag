package com.tag.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.acceptance.WithTestcontainers;
import com.tag.config.db.RedisTxContextConfig;
import com.tag.domain.auth.RefreshTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

@DataRedisTest
@Import(RedisTxContextConfig.class)
public class RefreshTokenRepositoryTest extends WithTestcontainers {

    @Autowired
    private RedisTemplate<String, Long> template;

    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void beforeEach() {
        refreshTokenRepository = new RefreshTokenRepository(template, 1000);
    }

    @AfterEach
    void tearDown() {
        template.delete("refreshToken");
    }

    @Test
    void Refresh_Token_을_저장한다() {
        // when
        refreshTokenRepository.save("refreshToken", 10L);

        // then
        final Long memberId = refreshTokenRepository.find("refreshToken");
        assertThat(memberId).isEqualTo(10L);
    }

    @Test
    void Refresh_Token_을_삭제한다() {
        // given
        refreshTokenRepository.save("refreshToken", 10L);

        // when
        refreshTokenRepository.delete("refreshToken");

        // then
        final Long memberId = template.opsForValue().get("refreshToken");
        assertThat(memberId).isNull();
    }

    @Test
    void Refresh_Token_이_저장되어_있으면_true_를_반환한다() {
        // given
        refreshTokenRepository.save("refreshToken", 10L);

        // when
        final boolean isExist = refreshTokenRepository.isExist("refreshToken");

        // then
        assertThat(isExist).isTrue();
    }

    @Test
    void Refresh_Token_이_저장되어_있으면_false_를_반환한다() {
        // when
        final boolean isExist = refreshTokenRepository.isExist("refreshToken");

        // then
        assertThat(isExist).isFalse();
    }

    @Test
    void Refresh_Token_을_조회한다() {
        // given
        refreshTokenRepository.save("refreshToken", 10L);

        // when
        final Long memberId = refreshTokenRepository.find("refreshToken");

        // then
        assertThat(memberId).isEqualTo(10L);
    }

    @Test
    void Refresh_Token_을_조회할때_Refresh_Token이_존재하지_않으면_예외가_발생한다() {
        assertThatThrownBy(
                () -> refreshTokenRepository.find("refreshToken")
        ).isExactlyInstanceOf(NullPointerException.class)
                .hasMessage("키가 존재하지 않거나 파이프라인/트랜잭션에 사용되고 있습니다.");
    }
}
