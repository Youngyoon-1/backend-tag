package com.tag.presentation;

import static com.tag.presentation.RefreshTokenCookieProvider.REFRESH_TOKEN;

import com.tag.application.AuthService;
import com.tag.domain.RefreshToken;
import com.tag.dto.response.AccessTokenResponse;
import com.tag.dto.response.IssueAccessTokenResult;
import com.tag.dto.response.LoginResponse;
import com.tag.dto.response.LoginResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenCookieProvider refreshTokenCookieProvider;

    public AuthController(final AuthService authService, final RefreshTokenCookieProvider refreshTokenCookieProvider) {
        this.authService = authService;
        this.refreshTokenCookieProvider = refreshTokenCookieProvider;
    }

    @GetMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestParam(required = false) final String code) {
        final LoginResult loginResult = authService.login(code);
        final String refreshToken = loginResult.getRefreshToken();
        final ResponseCookie cookie = refreshTokenCookieProvider.createCookie(refreshToken);
        final LoginResponse loginResponse = new LoginResponse(loginResult);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @DeleteMapping("/api/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken) {
        validateRefreshToken(refreshToken);
        authService.logout(refreshToken);
        final ResponseCookie logoutCookie = refreshTokenCookieProvider.createLogoutCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, logoutCookie.toString())
                .build();
    }

    @PostMapping("/api/token")
    public ResponseEntity<AccessTokenResponse> issueToken(
            @CookieValue(value = REFRESH_TOKEN, required = false) String refreshToken) {
        validateRefreshToken(refreshToken);
        final RefreshToken oldRefreshToken = authService.getRefreshToken(refreshToken);
        final IssueAccessTokenResult issueAccessTokenResult = authService.issueAccessToken(oldRefreshToken);
        final String newRefreshToken = issueAccessTokenResult.getRefreshToken();
        final ResponseCookie cookie = refreshTokenCookieProvider.createCookie(newRefreshToken);
        final AccessTokenResponse accessTokenResponse = new AccessTokenResponse(issueAccessTokenResult);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessTokenResponse);
    }

    private void validateRefreshToken(final String refreshToken) {
        if (null == refreshToken) {
            throw new RuntimeException("리프레시 토큰이 존재하지 않습니다.");
        }
    }
}
