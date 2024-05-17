package com.tag.presentation.auth;

import com.tag.application.auth.AuthService;
import com.tag.domain.auth.RefreshToken;
import com.tag.dto.response.auth.AccessTokenResponse;
import com.tag.dto.response.auth.IssueAccessTokenResult;
import com.tag.dto.response.auth.LoginResponse;
import com.tag.dto.response.auth.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public final class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    public AuthController(final AuthService authService, final RefreshTokenCookieProvider refreshTokenCookieProvider) {
        this.authService = authService;
        this.refreshTokenCookieProvider = refreshTokenCookieProvider;
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam(name = "code") final String code) {
        final LoginResult loginResult = authService.login(code);
        final String refreshToken = loginResult.refreshToken();
        final ResponseCookie refreshTokenCookie = refreshTokenCookieProvider.createCookie(refreshToken);
        final LoginResponse loginResponse = new LoginResponse(loginResult);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        final ResponseCookie logoutCookie = refreshTokenCookieProvider.createLogoutCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .build();
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> issueToken(
            @CookieValue(value = "refreshToken") String refreshToken) {
        // 레디스 트랜잭션 처리에 따른 조회로직 분리
        final RefreshToken oldRefreshToken = authService.getRefreshToken(refreshToken);
        final IssueAccessTokenResult issueAccessTokenResult = authService.issueAccessToken(oldRefreshToken);
        final String newRefreshToken = issueAccessTokenResult.refreshToken();
        final ResponseCookie refreshTokenCookie = refreshTokenCookieProvider.createCookie(newRefreshToken);
        final AccessTokenResponse accessTokenResponse = new AccessTokenResponse(issueAccessTokenResult);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(accessTokenResponse);
    }
}
