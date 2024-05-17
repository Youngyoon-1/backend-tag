package com.tag.application.auth;

import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.domain.auth.RefreshToken;
import com.tag.domain.auth.RefreshTokenRepository;
import com.tag.dto.response.auth.IssueAccessTokenResult;
import com.tag.dto.response.auth.LoginResult;
import com.tag.dto.response.auth.OauthProfileResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String FAIL_ISSUE_ACCESS_TOKEN_NO_EXIST_MEMBER = "엑세스 토큰을 발급할 수 없습니다. 존재하지 않는 회원입니다.";

    private final OauthClient oauthClient;
    private final MemberRepository memberRepository;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(final OauthClient oauthClient, final MemberRepository memberRepository,
                       final AccessTokenProvider accessTokenProvider,
                       final RefreshTokenRepository refreshTokenRepository) {
        this.oauthClient = oauthClient;
        this.memberRepository = memberRepository;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public LoginResult login(final String code) {
        final OauthProfileResponse oauthProfileResponse = oauthClient.requestProfile(code);
        final String email = oauthProfileResponse.email();
        final Member savedMember = memberRepository.findByEmail(email)
                .orElseGet(
                        () -> {
                            final Member member = Member.createForSave(email);
                            return memberRepository.save(member);
                        }
                );
        final String refreshToken = UUID.randomUUID()
                .toString();
        final long memberId = savedMember.getId();
        refreshTokenRepository.save(refreshToken, memberId);
        final String accessToken = accessTokenProvider.issueToken(memberId);
        return new LoginResult(savedMember, accessToken, refreshToken);
    }

    public void logout(final String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public RefreshToken getRefreshToken(final String refreshToken) {
        final long memberId = refreshTokenRepository.find(refreshToken);
        return new RefreshToken(refreshToken, memberId);
    }

    @Transactional
    public IssueAccessTokenResult issueAccessToken(final RefreshToken refreshToken) {
        final String refreshTokenValue = refreshToken.refreshToken();
        refreshTokenRepository.delete(refreshTokenValue);
        final String newRefreshToken = UUID.randomUUID()
                .toString();
        final long memberId = refreshToken.memberId();
        refreshTokenRepository.save(newRefreshToken, memberId);
        final String accessToken = accessTokenProvider.issueToken(memberId);
        final boolean isRegistered = memberRepository.isRegistered(memberId)
                .orElseThrow(() -> new IllegalArgumentException(FAIL_ISSUE_ACCESS_TOKEN_NO_EXIST_MEMBER));
        return new IssueAccessTokenResult(newRefreshToken, accessToken, isRegistered);
    }
}
