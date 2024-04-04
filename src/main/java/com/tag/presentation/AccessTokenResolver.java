package com.tag.presentation;

import com.tag.application.AccessTokenProvider;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AccessTokenResolver implements HandlerMethodArgumentResolver {

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
        return accessTokenProvider.getMemberId(authorizationHeader);
    }
}
