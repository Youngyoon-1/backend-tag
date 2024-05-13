package com.tag.config.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final String PATH_PATTERN_ALLOW_ALL = "/**";
    private static final String CORS_ALLOWED_METHODS = "GET,POST,HEAD,PUT,PATCH,DELETE,TRACE,OPTIONS";
    private static final String SEPARATOR_CORS_ALLOWED_METHODS = ",";

    private final String frontDomain;

    public CorsConfig(@Value("${tag.front-domain}") final String frontDomain) {
        this.frontDomain = frontDomain;
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping(PATH_PATTERN_ALLOW_ALL)
                .allowedMethods(CORS_ALLOWED_METHODS.split(SEPARATOR_CORS_ALLOWED_METHODS))
                .allowedOrigins(frontDomain)
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION, HttpHeaders.SET_COOKIE);
    }
}
