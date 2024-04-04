package com.tag.config;

import com.tag.presentation.AccessTokenResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String CORS_ALLOWED_METHODS = "GET,POST,HEAD,PUT,PATCH,DELETE,TRACE,OPTIONS";
    private final AccessTokenResolver accessTokenResolver;

    public WebConfig(final AccessTokenResolver accessTokenResolver) {
        this.accessTokenResolver = accessTokenResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenResolver);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedMethods(CORS_ALLOWED_METHODS.split(","))
                .allowedOrigins("http://localhost:3000", "http://192.168.0.4:3000", "http://172.30.18.154:3000", "https://frontend-tag-psi.vercel.app")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.SET_COOKIE);
    }
}
