package com.tag.presentation.auth;

import com.tag.application.auth.AccessTokenProvider;
import java.util.Objects;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public final class AccessTokenResolver implements HandlerMethodArgumentResolver {

    private static final String FAIL_EXTRACT_TOKEN_AUTH_HEADER_NULL = "인증 헤더가 존재하지 않아 토큰을 추출할 수 없습니다.";

    private final AccessTokenProvider accessTokenProvider;

    public AccessTokenResolver(final AccessTokenProvider accessTokenProvider) {
        this.accessTokenProvider = accessTokenProvider;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AccessTokenValue.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        Objects.requireNonNull(authorizationHeader, FAIL_EXTRACT_TOKEN_AUTH_HEADER_NULL);
        return accessTokenProvider.getMemberId(authorizationHeader);
    }
}
