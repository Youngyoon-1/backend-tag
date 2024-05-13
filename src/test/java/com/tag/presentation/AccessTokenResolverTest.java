package com.tag.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import com.tag.application.auth.AccessTokenProvider;
import com.tag.presentation.auth.AccessTokenResolver;
import com.tag.presentation.auth.AccessTokenValue;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

@ExtendWith(MockitoExtension.class)
class AccessTokenResolverTest {

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @InjectMocks
    private AccessTokenResolver accessTokenResolver;

    // Method 객체 생성을 위한 메서드
    private void useAccessTokenValueMethod(@AccessTokenValue String memberId) {
    }

    // Method 객체 생성을 위한 메서드
    private void noUseAccessTokenValueMethod(String memberId) {
    }

    @Test
    void MethodParameter_에_AccessTokenValue_어노테이션이_붙어있는_경우_true_를_반환한다()
            throws NoSuchMethodException {
        // when
        final Method method = this.getClass()
                .getDeclaredMethod("useAccessTokenValueMethod", String.class);
        final MethodParameter methodParameter = new MethodParameter(method, 0);
        final boolean supportsParameter = accessTokenResolver.supportsParameter(methodParameter);

        // then
        assertThat(supportsParameter).isTrue();
    }

    @Test
    void MethodParameter_에_AccessTokenValue_어노테이션이_붙어있지_않은_경우_false_를_반환한다() throws NoSuchMethodException {
        // when
        final Method method = this.getClass()
                .getDeclaredMethod("noUseAccessTokenValueMethod", String.class);
        final MethodParameter methodParameter = new MethodParameter(method, 0);
        final boolean supportsParameter = accessTokenResolver.supportsParameter(methodParameter);

        // then
        assertThat(supportsParameter).isFalse();
    }

    @Test
    void 요청_인가_헤더에_있는_엑세스_토큰값을_멤버_아이디로_변환한다() {
        // given
        BDDMockito.given(accessTokenProvider.getMemberId("accessToken"))
                .willReturn(10L);

        // when
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader(HttpHeaders.AUTHORIZATION, "accessToken");
        final ServletWebRequest servletWebRequest = new ServletWebRequest(mockHttpServletRequest);
        final Object resolvedArgument = accessTokenResolver.resolveArgument(null, null, servletWebRequest,
                null);

        //then
        final Long memberId = Long.valueOf(resolvedArgument.toString());
        Assertions.assertAll(
                () -> assertThat(memberId).isEqualTo(10L),
                () -> BDDMockito.verify(accessTokenProvider).getMemberId("accessToken")
        );
    }
}
