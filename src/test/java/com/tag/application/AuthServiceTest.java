package com.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.domain.RefreshToken;
import com.tag.domain.RefreshTokenRepository;
import com.tag.dto.response.GoogleProfileResponse;
import com.tag.dto.response.IssueAccessTokenResult;
import com.tag.dto.response.LoginResult;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private GoogleOauthClient googleOauthClient;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private S3ObjectManager s3ObjectManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인을_한다_회원정보가_기존에_이미_저장되어_있는_경우() {
        // given
        final GoogleProfileResponse googleProfileResponse = new GoogleProfileResponse("test1@test.com");
        BDDMockito.given(googleOauthClient.requestProfile("testCode"))
                .willReturn(googleProfileResponse);
        final Member member = Member.builder()
                .id(10L)
                .email("test2@test.com")
                .profileImageName("profileImageName")
                .qrImageName("qrImageName")
                .build();
        BDDMockito.given(memberRepository.findByEmail("test1@test.com"))
                .willReturn(Optional.of(member));
        BDDMockito.given(refreshTokenProvider.issueToken())
                .willReturn("refreshToken");
        BDDMockito.willDoNothing()
                .given(refreshTokenRepository)
                .save("refreshToken", 10L);
        BDDMockito.given(accessTokenProvider.issueToken(10L))
                .willReturn("accessToken");
        BDDMockito.given(
                        s3ObjectManager.createPresignedGetUrl("profileImageName", MemberImageCategory.PROFILE))
                .willReturn("profileImageUrl");
        BDDMockito.given(s3ObjectManager.createPresignedGetUrl("qrImageName", MemberImageCategory.QR))
                .willReturn("qrImageUrl");

        // when
        final LoginResult loginResult = authService.login("testCode");

        // then
        final String email = loginResult.getEmail();
        final String introductoryArticle = loginResult.getIntroductoryArticle();
        final String profileImageUrl = loginResult.getProfileImageUrl();
        final String qrImageUrl = loginResult.getQrImageUrl();
        final String qrLinkUrl = loginResult.getQrLinkUrl();
        final String accessToken = loginResult.getAccessToken();
        final String refreshTokenValue = loginResult.getRefreshToken();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test1@test.com"),
                () -> assertThat(introductoryArticle).isNull(),
                () -> assertThat(profileImageUrl).isEqualTo("profileImageUrl"),
                () -> assertThat(qrImageUrl).isEqualTo("qrImageUrl"),
                () -> assertThat(qrLinkUrl).isNull(),
                () -> assertThat(accessToken).isEqualTo("accessToken"),
                () -> assertThat(refreshTokenValue).isEqualTo("refreshToken"),
                () -> verify(googleOauthClient).requestProfile("testCode"),
                () -> verify(memberRepository).findByEmail("test1@test.com"),
                () -> verify(refreshTokenProvider).issueToken(),
                () -> verify(refreshTokenRepository).save("refreshToken", 10L),
                () -> verify(accessTokenProvider).issueToken(10L),
                () -> verify(s3ObjectManager).createPresignedGetUrl("profileImageName",
                        MemberImageCategory.PROFILE),
                () -> verify(s3ObjectManager).createPresignedGetUrl("qrImageName", MemberImageCategory.QR)
        );
    }

    @Test
    void 로그인을_한다_최초_로그인인_경우() {
        // given
        final GoogleProfileResponse googleProfileResponse = new GoogleProfileResponse("test@test.com");
        BDDMockito.given(googleOauthClient.requestProfile("testCode"))
                .willReturn(googleProfileResponse);
        BDDMockito.given(memberRepository.findByEmail("test@test.com"))
                .willReturn(Optional.empty());
        final Member member = Member.builder()
                .id(10L)
                .email("test@test.com")
                .build();
        BDDMockito.given(memberRepository.save(any(Member.class)))
                .willReturn(member);
        BDDMockito.given(refreshTokenProvider.issueToken())
                .willReturn("refreshToken");
        BDDMockito.willDoNothing()
                .given(refreshTokenRepository)
                .save("refreshToken", 10L);
        BDDMockito.given(accessTokenProvider.issueToken(10L))
                .willReturn("accessToken");

        // when
        final LoginResult loginResult = authService.login("testCode");

        // then
        final String email = loginResult.getEmail();
        final String accessToken = loginResult.getAccessToken();
        final String refreshTokenValue = loginResult.getRefreshToken();
        Assertions.assertAll(
                () -> assertThat(email).isEqualTo("test@test.com"),
                () -> assertThat(refreshTokenValue).isEqualTo("refreshToken"),
                () -> assertThat(accessToken).isEqualTo("accessToken"),
                () -> verify(googleOauthClient).requestProfile("testCode"),
                () -> verify(memberRepository).findByEmail("test@test.com"),
                () -> verify(memberRepository).save(any(Member.class)),
                () -> verify(refreshTokenProvider).issueToken(),
                () -> verify(refreshTokenRepository).save("refreshToken", 10L),
                () -> verify(accessTokenProvider).issueToken(10L)
        );
    }

    @Test
    void 로그아웃을_한다() {
        // given
        BDDMockito.willDoNothing()
                .given(refreshTokenRepository)
                .delete("refreshToken");

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
                () -> assertThat(memberId).isEqualTo(10L),
                () -> BDDMockito.verify(refreshTokenRepository)
                        .find("refreshToken")
        );
    }

    @Test
    void 리프레시_토큰을_조회한다_리프레시_토큰이_존재하지_않으면_예외가_발생한다() {
        // given
        BDDMockito.given(refreshTokenRepository.find("refreshToken"))
                .willReturn(null);

        // when, then
        Assertions.assertAll(
                () -> assertThatThrownBy(
                        () -> authService.getRefreshToken("refreshToken")
                ).isExactlyInstanceOf(RuntimeException.class)
                        .hasMessage("리프레시 토큰을 조회하는 과정에서 예외가 발생했습니다."),
                () -> BDDMockito.verify(refreshTokenRepository)
                        .find("refreshToken")
        );
    }

    @Test
    void 리프레시_토큰으로_엑세스_토큰을_발행한다() {
        // given
        BDDMockito.willDoNothing()
                .given(refreshTokenRepository)
                .delete("oldRefreshToken");
        BDDMockito.given(refreshTokenProvider.issueToken())
                .willReturn("newRefreshToken");
        BDDMockito.willDoNothing()
                .given(refreshTokenRepository)
                .save("newRefreshToken", 10L);
        BDDMockito.given(accessTokenProvider.issueToken(10L))
                .willReturn("accessToken");

        // when
        final RefreshToken refreshToken = new RefreshToken("oldRefreshToken", 10L);
        final IssueAccessTokenResult issueAccessTokenResult = authService.issueAccessToken(refreshToken);

        // then
        final String refreshTokenValue = issueAccessTokenResult.getRefreshToken();
        final String accessToken = issueAccessTokenResult.getAccessToken();
        Assertions.assertAll(
                () -> assertThat(refreshTokenValue).isEqualTo("newRefreshToken"),
                () -> assertThat(accessToken).isEqualTo("accessToken")
        );
    }
}
