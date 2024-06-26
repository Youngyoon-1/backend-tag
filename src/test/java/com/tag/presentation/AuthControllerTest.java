package com.tag.presentation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tag.application.auth.AuthService;
import com.tag.domain.auth.RefreshToken;
import com.tag.domain.member.Member;
import com.tag.dto.response.auth.AccessTokenResponse;
import com.tag.dto.response.auth.IssueAccessTokenResult;
import com.tag.dto.response.auth.LoginResponse;
import com.tag.dto.response.auth.LoginResult;
import com.tag.presentation.auth.AccessTokenResolver;
import com.tag.presentation.auth.AuthController;
import com.tag.presentation.auth.RefreshTokenCookieProvider;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenCookieProvider refreshTokenCookieProvider;

    @MockBean
    private AccessTokenResolver accessTokenResolver;

    @Test
    void 로그인_한다() throws Exception {
        // given
        final Member member = Member.builder()
                .email("test@test.com")
                .build();
        final LoginResult loginResult = new LoginResult(member, "accessToken", "refreshToken");
        BDDMockito.given(authService.login("testCode"))
                .willReturn(loginResult);
        final ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "refreshToken")
                .build();
        BDDMockito.given(refreshTokenCookieProvider.createCookie("refreshToken"))
                .willReturn(responseCookie);

        // when
        final ResultActions resultActions = mockMvc.perform(
                get("/api/login?code=testCode")
        );

        // then
        final LoginResponse loginResponse = new LoginResponse(loginResult);
        final String serializedExpectedContent = objectMapper.writeValueAsString(loginResponse);
        resultActions.andExpectAll(
                status().isOk(),
                header().string("Set-cookie", "refreshToken=refreshToken"),
                content().string(serializedExpectedContent)
        ).andDo(print());

        assertAll(
                () -> BDDMockito.verify(authService).login("testCode"),
                () -> BDDMockito.verify(refreshTokenCookieProvider).createCookie("refreshToken")
        );
    }

    @Test
    void 로그아웃_한다() throws Exception {
        // given
        BDDMockito.willDoNothing()
                .given(authService)
                .logout("refreshToken");
        final ResponseCookie logoutCookie = ResponseCookie.from("refreshToken", "")
                .build();
        BDDMockito.given(refreshTokenCookieProvider.createLogoutCookie())
                .willReturn(logoutCookie);

        // when
        final Cookie refreshToken = new Cookie("refreshToken", "refreshToken");
        final ResultActions resultActions = mockMvc.perform(
                delete("/api/logout")
                        .cookie(refreshToken)
        );

        // then
        resultActions.andExpectAll(
                status().isNoContent(),
                header().string("Set-cookie", "refreshToken=")
        ).andDo(print());

        assertAll(
                () -> BDDMockito.verify(authService).logout("refreshToken"),
                () -> BDDMockito.verify(refreshTokenCookieProvider).createLogoutCookie()
        );
    }

    @Test
    void 엑세스_토큰을_발급한다() throws Exception {
        // given
        final RefreshToken refreshToken = new RefreshToken("refreshToken", 10L);
        BDDMockito.given(authService.getRefreshToken("refreshToken"))
                .willReturn(refreshToken);
        final IssueAccessTokenResult issueAccessTokenResult = new IssueAccessTokenResult("newRefreshToken",
                "accessToken", true);
        BDDMockito.given(authService.issueAccessToken(refreshToken))
                .willReturn(issueAccessTokenResult);
        final ResponseCookie responseCookie = ResponseCookie.from("refreshToken", "newRefreshToken")
                .build();
        BDDMockito.given(refreshTokenCookieProvider.createCookie("newRefreshToken"))
                .willReturn(responseCookie);

        // when
        final Cookie cookie = new Cookie("refreshToken", "refreshToken");
        final ResultActions resultActions = mockMvc.perform(
                post("/api/token")
                        .cookie(cookie)
        );

        // then
        final AccessTokenResponse accessTokenResponse = new AccessTokenResponse(
                new IssueAccessTokenResult("refreshToken", "accessToken", true));
        final String serializedExpectedContent = objectMapper.writeValueAsString(accessTokenResponse);
        resultActions.andExpectAll(
                status().isOk(),
                header().string(HttpHeaders.SET_COOKIE, "refreshToken=newRefreshToken"),
                content().string(serializedExpectedContent)
        ).andDo(print());
        assertAll(
                () -> BDDMockito.verify(authService).getRefreshToken("refreshToken"),
                () -> BDDMockito.verify(authService).issueAccessToken(refreshToken),
                () -> BDDMockito.verify(refreshTokenCookieProvider).createCookie("newRefreshToken")
        );
    }
}
