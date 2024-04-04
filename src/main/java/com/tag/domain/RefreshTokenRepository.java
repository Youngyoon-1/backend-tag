package com.tag.domain;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenRepository {

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
        if (isExist == null) {
            throw new RuntimeException("레디스 키를 조회할 수 없습니다.");
        }
        return isExist;
    }

    public Long find(final String refreshToken) {
        return redisTemplate.opsForValue()
                .get(refreshToken);
    }
}
