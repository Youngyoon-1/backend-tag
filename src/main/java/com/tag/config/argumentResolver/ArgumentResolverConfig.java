package com.tag.config.argumentResolver;

import com.tag.presentation.auth.AccessTokenResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ArgumentResolverConfig implements WebMvcConfigurer {

    private final AccessTokenResolver accessTokenResolver;

    public ArgumentResolverConfig(final AccessTokenResolver accessTokenResolver) {
        this.accessTokenResolver = accessTokenResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenResolver);
    }
}
