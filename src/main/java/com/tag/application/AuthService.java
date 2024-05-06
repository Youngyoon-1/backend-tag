package com.tag.application;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.domain.RefreshToken;
import com.tag.domain.RefreshTokenRepository;
import com.tag.dto.response.IssueAccessTokenResult;
import com.tag.dto.response.LoginResult;
import com.tag.dto.response.OauthProfileResponse;
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
    private final ObjectStorageManager objectStorageManager;

    public AuthService(final OauthClient oauthClient, final MemberRepository memberRepository,
                       final AccessTokenProvider accessTokenProvider,
                       final RefreshTokenRepository refreshTokenRepository,
                       final ObjectStorageManager objectStorageManager) {
        this.oauthClient = oauthClient;
        this.memberRepository = memberRepository;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.objectStorageManager = objectStorageManager;
    }

    @Transactional
    public LoginResult login(final String code) {
        final OauthProfileResponse oauthProfileResponse = oauthClient.getProfile(code);
        final String email = oauthProfileResponse.getEmail();
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
        final String profileImageName = savedMember.getProfileImageName();
        final String profileImageUrl = objectStorageManager.createGetUrl(profileImageName, MemberImageCategory.PROFILE);
        final String qrImageName = savedMember.getQrImageName();
        final String qrImageUrl = objectStorageManager.createGetUrl(qrImageName, MemberImageCategory.QR);
        return new LoginResult(savedMember, profileImageUrl, qrImageUrl, accessToken, refreshToken);
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
        final String refreshTokenValue = refreshToken.getRefreshToken();
        refreshTokenRepository.delete(refreshTokenValue);
        final String newRefreshToken = UUID.randomUUID()
                .toString();
        final long memberId = refreshToken.getMemberId();
        refreshTokenRepository.save(newRefreshToken, memberId);
        final String accessToken = accessTokenProvider.issueToken(memberId);
        final boolean isRegistered = memberRepository.isRegistered(memberId)
                .orElseThrow(() -> new IllegalArgumentException(FAIL_ISSUE_ACCESS_TOKEN_NO_EXIST_MEMBER));
        return new IssueAccessTokenResult(newRefreshToken, accessToken, isRegistered);
    }
}
