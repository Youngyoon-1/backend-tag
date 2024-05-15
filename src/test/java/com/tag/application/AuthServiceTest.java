package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.tag.application.auth.AccessTokenProvider;
import com.tag.application.auth.AuthService;
import com.tag.application.auth.OauthClient;
import com.tag.domain.auth.RefreshToken;
import com.tag.domain.auth.RefreshTokenRepository;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.response.auth.IssueAccessTokenResult;
import com.tag.dto.response.auth.LoginResult;
import com.tag.dto.response.auth.OauthProfileResponse;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private OauthClient oauthClient;

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private AccessTokenProvider accessTokenProvider = new AccessTokenProvider(
            "secretKeysecretKeysecretKeysecretKeysecretKeysecretKey", 10000);

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인을_한다_회원정보가_기존에_이미_저장되어_있는_경우() {
        // given
        final OauthProfileResponse oauthProfileResponse = new OauthProfileResponse("test1@test.com");
        BDDMockito.given(oauthClient.requestProfile("testCode"))
                .willReturn(oauthProfileResponse);
        final Member member = Member.builder()
                .id(1L)
                .email("test1@test.com")
                .profileImageName("profileImageName")
                .build();
        BDDMockito.given(memberRepository.findByEmail("test1@test.com"))
                .willReturn(Optional.of(member));

        // when
        final LoginResult loginResult = authService.login("testCode");

        // then
        final boolean isRegistered = loginResult.isRegistered();
        final String accessToken = loginResult.getAccessToken();
        final String refreshTokenValue = loginResult.getRefreshToken();
        Assertions.assertAll(
                () -> assertThat(isRegistered).isFalse(),
                () -> assertThat(accessToken).isExactlyInstanceOf(String.class),
                () -> assertThat(refreshTokenValue).isExactlyInstanceOf(String.class),
                () -> verify(oauthClient).requestProfile("testCode"),
                () -> verify(refreshTokenRepository).save(any(String.class), eq(1L))
        );
    }

    @Test
    void 로그인을_한다_최초_로그인인_경우() {
        // given
        final OauthProfileResponse oauthProfileResponse = new OauthProfileResponse("test@test.com");
        BDDMockito.given(oauthClient.requestProfile("testCode"))
                .willReturn(oauthProfileResponse);
        BDDMockito.given(memberRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());
        final Member member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .build();
        BDDMockito.given(memberRepository.save(any(Member.class)))
                .willReturn(member);

        // when
        final LoginResult loginResult = authService.login("testCode");

        // then
        final boolean isRegistered = loginResult.isRegistered();
        final String accessToken = loginResult.getAccessToken();
        final String refreshTokenValue = loginResult.getRefreshToken();
        Assertions.assertAll(
                () -> assertThat(isRegistered).isFalse(),
                () -> assertThat(refreshTokenValue).isExactlyInstanceOf(String.class),
                () -> assertThat(accessToken).isExactlyInstanceOf(String.class),
                () -> verify(oauthClient).requestProfile("testCode"),
                () -> verify(memberRepository).save(any(Member.class)),
                () -> verify(refreshTokenRepository).save(any(String.class), eq(1L))
        );
    }

    @Test
    void 로그아웃을_한다() {
        // when
        authService.logout("refreshToken");

        // then
        BDDMockito.verify(refreshTokenRepository)
                .delete("refreshToken");
    }

    @Test
    void 리프레시_토큰을_조회한다() {
        // given
        BDDMockito.given(refreshTokenRepository.find("refreshToken"))
                .willReturn(10L);

        // when
        final RefreshToken refreshToken = authService.getRefreshToken("refreshToken");

        // then
        final String refreshTokenValue = refreshToken.getRefreshToken();
        final Long memberId = refreshToken.getMemberId();
        Assertions.assertAll(
                () -> assertThat(refreshTokenValue).isEqualTo("refreshToken"),
                () -> assertThat(memberId).isEqualTo(10L)
        );
    }

    @Test
    void 리프레시_토큰으로_엑세스_토큰을_발행한다() {
        // given
        BDDMockito.given(memberRepository.isRegistered(10L))
                .willReturn(Optional.of(true));
        final RefreshToken refreshToken = new RefreshToken("oldRefreshToken", 10L);

        // when
        final IssueAccessTokenResult issueAccessTokenResult = authService.issueAccessToken(refreshToken);

        // then
        final String refreshTokenValue = issueAccessTokenResult.getRefreshToken();
        final String accessToken = issueAccessTokenResult.getAccessToken();
        final boolean isRegistered = issueAccessTokenResult.isRegistered();
        Assertions.assertAll(
                () -> assertThat(isRegistered).isTrue(),
                () -> assertThat(refreshTokenValue).isExactlyInstanceOf(String.class),
                () -> assertThat(accessToken).isExactlyInstanceOf(String.class),
                () -> BDDMockito.verify(refreshTokenRepository).delete("oldRefreshToken"),
                () -> BDDMockito.verify(refreshTokenRepository).save(any(String.class), eq(10L))
        );
    }

    @Test
    void 엑세스_토큰_발급시_존재하지_않는_회원인_경우_예외가_발생한다() {
        // given
        BDDMockito.given(memberRepository.isRegistered(10L))
                .willReturn(Optional.empty());
        final RefreshToken refreshToken = new RefreshToken("refreshToken", 10L);

        // when, then
        assertThatThrownBy(() -> authService.issueAccessToken(refreshToken))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("엑세스 토큰을 발급할 수 없습니다. 존재하지 않는 회원입니다.");
    }
}
