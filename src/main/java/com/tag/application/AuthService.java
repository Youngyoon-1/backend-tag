package com.tag.application;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.domain.RefreshToken;
import com.tag.domain.RefreshTokenRepository;
import com.tag.dto.response.GoogleProfileResponse;
import com.tag.dto.response.IssueAccessTokenResult;
import com.tag.dto.response.LoginResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final GoogleOauthClient googleOauthClient;
    private final MemberRepository memberRepository;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectStorageManager objectStorageManager;

    public AuthService(final GoogleOauthClient googleOauthClient, final MemberRepository memberRepository,
                       final RefreshTokenProvider refreshTokenProvider,
                       final AccessTokenProvider accessTokenProvider,
                       final RefreshTokenRepository refreshTokenRepository,
                       final ObjectStorageManager objectStorageManager) {
        this.googleOauthClient = googleOauthClient;
        this.memberRepository = memberRepository;
        this.refreshTokenProvider = refreshTokenProvider;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.objectStorageManager = objectStorageManager;
    }

    @Transactional
    public LoginResult login(final String code) {
        if (code == null) {
            throw new RuntimeException("로그인 코드가 존재하지 않습니다.");
        }
        final GoogleProfileResponse googleProfileResponse = googleOauthClient.requestProfile(code);
        final Member member = googleProfileResponse.toMember();
        final Member savedMember = memberRepository.findByEmail(member.getEmail())
                .orElseGet(() -> memberRepository.save(member));
        savedMember.update(member);
        final long memberId = savedMember.getId();
        final String refreshToken = refreshTokenProvider.issueToken();
        refreshTokenRepository.save(refreshToken, memberId);
        final String accessToken = accessTokenProvider.issueToken(memberId);
        final String profileImageName = savedMember.getProfileImageName();
        final String profileImageUrl = getUrl(profileImageName, MemberImageCategory.PROFILE);
        final String qrImageName = savedMember.getQrImageName();
        final String qrImageUrl = getUrl(qrImageName, MemberImageCategory.QR);
        return new LoginResult(savedMember, profileImageUrl, qrImageUrl, accessToken, refreshToken);
    }

    private String getUrl(final String imageName, final MemberImageCategory memberImageCategory) {
        if (imageName != null) {
            return objectStorageManager.createPresignedGetUrl(imageName,
                    memberImageCategory);
        }
        return null;
    }

    public void logout(final String refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(final String refreshToken) {
        final Long memberId = refreshTokenRepository.find(refreshToken);
        if (memberId == null) {
            throw new RuntimeException("리프레시 토큰을 조회하는 과정에서 예외가 발생했습니다.");
        }
        return new RefreshToken(refreshToken, memberId);
    }

    @Transactional
    public IssueAccessTokenResult issueAccessToken(final RefreshToken refreshToken) {
        final Long memberId = refreshToken.getMemberId();
        final String refreshTokenValue = refreshToken.getRefreshToken();
        refreshTokenRepository.delete(refreshTokenValue);
        final String newRefreshToken = refreshTokenProvider.issueToken();
        refreshTokenRepository.save(newRefreshToken, memberId);
        final String accessToken = accessTokenProvider.issueToken(memberId);
        final Boolean isRegistered = memberRepository.isRegistered(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return new IssueAccessTokenResult(newRefreshToken, accessToken, isRegistered);
    }
}
