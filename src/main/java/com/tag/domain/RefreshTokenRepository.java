package com.tag.domain;

import java.time.Duration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public final class RefreshTokenRepository {

    private static final String DOES_NOT_EXIST_OR_USED_IN_PIPELINE_TRANSACTION = "키가 존재하지 않거나 파이프라인/트랜잭션에 사용되고 있습니다.";

    private final RedisTemplate<String, Long> redisTemplate;
    private final long expireLength;

    public RefreshTokenRepository(final RedisTemplate<String, Long> redisTemplate,
                                  @Value("${refresh-token.expire-length}") final long expireLength) {
        this.redisTemplate = redisTemplate;
        this.expireLength = expireLength;
    }

    public void save(final String refreshToken, final long memberId) {
        redisTemplate.opsForValue().set(refreshToken, memberId);
        redisTemplate.expire(refreshToken, Duration.ofMillis(expireLength));
    }

    public void delete(final String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    public boolean isExist(final String refreshToken) {
        final Boolean isExist = redisTemplate.hasKey(refreshToken);
        Objects.requireNonNull(isExist, DOES_NOT_EXIST_OR_USED_IN_PIPELINE_TRANSACTION);
        return isExist;
    }

    public long find(final String refreshToken) {
        final Long memberId = redisTemplate.opsForValue()
                .get(refreshToken);
        Objects.requireNonNull(memberId, DOES_NOT_EXIST_OR_USED_IN_PIPELINE_TRANSACTION);
        return memberId;
    }
}
