package com.tag.config.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RedisTxContextConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(@Value("${spring.data.redis.host}") final String host,
                                                         @Value("${spring.data.redis.port}") final int port,
                                                         @Value("${spring.data.redis.password}") final String password) {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        return new LettuceConnectionFactory(config);
    }

    @Primary
    @Bean
    public RedisTemplate<String, Long> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setEnableTransactionSupport(true);
        return template;
    }
}
