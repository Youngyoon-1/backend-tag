package com.tag.application;

import static com.tag.application.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessTokenProviderTest {

    private final AuthTokenExtractor authTokenExtractor = new AuthTokenExtractor();

    private final AccessTokenProvider accessTokenProvider = new AccessTokenProvider(
            authTokenExtractor, "secretKeySecretKeySecretKeySecretKeySecretKey", 1000
    );

    @Test
    void 엑세스_토큰을_발급한다() {
        // when
        final String accessToken = accessTokenProvider.issueToken(10L);

        // then
        final Key secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode("secretKeySecretKeySecretKeySecretKeySecretKey"));
        final JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        final Jws<Claims> claims = jwtParser.parseClaimsJws(accessToken);
        final JwsHeader jwsHeader = claims.getHeader();
        final String algorithm = jwsHeader.getAlgorithm();
        final Claims body = claims.getBody();
        final String subject = body.getSubject();
        final Date issuedAt = body.getIssuedAt();
        final Date expiration = body.getExpiration();
        final long memberId = body.get("memberId", Long.class);
        Assertions.assertAll(
                () -> assertThat(algorithm).isEqualTo("HS256"),
                () -> assertThat(subject).isEqualTo("AccessToken"),
                () -> assertThat(issuedAt).isNotNull(),
                () -> assertThat(expiration).isNotNull(),
                () -> assertThat(memberId).isEqualTo(10L)
        );
    }

    @Test
    void authorization_header_에서_멤버_아이디를_추출해서_반환한다() {
        // given
        final String accessToken = accessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;

        // when
        final Long memberId = accessTokenProvider.getMemberId(authorizationHeader);

        // then
        assertThat(memberId).isEqualTo(10L);
    }

    @Test
    void authorization_header_에서_멤버_아이디를_추출해서_반환한다_토큰_형식이_유효하지_않으면_예외가_발생한다() {
        final String authorizationHeader = TOKEN_TYPE + " " + "invalidToken";
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰 값이 유효하지 않아 추출할 수 없습니다.");
    }

    @Test
    void authorization_header_에서_멤버_아이디를_추출해서_반환한다_만료된_토큰인_경우_예외가_발생한다() {
        final AccessTokenProvider otherAccessTokenProvider = new AccessTokenProvider(
                authTokenExtractor, "otherSecretKeyOtherSecretKeyOtherSecretKeyOtherSecretKey", 1
        );
        final String accessToken = otherAccessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        assertThatThrownBy(
                () -> otherAccessTokenProvider.getMemberId(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 만료되었습니다.");
    }

    @Test
    void 형식이_유효하지_않은_엑세스_토큰을_검증하면_예외가_발생한다() {
        final String authorizationHeader = TOKEN_TYPE + " " + "malformedAccessToken";
        assertThatThrownBy(
                () -> accessTokenProvider.validateAuthHeader(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    void 다른_비밀키로_생성된_엑세스_토큰을_검증하면_예외가_발생한다() {
        final AccessTokenProvider otherAccessTokenProvider = new AccessTokenProvider(
                authTokenExtractor, "otherSecretKeyOtherSecretKeyOtherSecretKeyOtherSecretKey", 1000
        );
        final String accessToken = otherAccessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        assertThatThrownBy(
                () -> accessTokenProvider.validateAuthHeader(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    void 변조된_엑세스_토큰을_검증하면_예외가_발생한다() {
        // https://jwt.io 에서 memberId 를 1 로 변경한 토큰값
        final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsImlhdCI6MTcwMzY1NTEzMSwiZXhwIjoxNzAzNjU1MTMyLCJtZW1iZXJJZCI6MX0.nhaiQ3UdmjZK7Kt_mJqW8txMs1--xPjcnUZJ5X7kSB8";
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        assertThatThrownBy(
                () -> accessTokenProvider.validateAuthHeader(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    void 만료된_엑세스_토큰을_검증하면_예외가_발생한다() {
        final AccessTokenProvider otherAccessTokenProvider = new AccessTokenProvider(
                authTokenExtractor, "otherSecretKeyOtherSecretKeyOtherSecretKeyOtherSecretKey", 1
        );
        final String accessToken = otherAccessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        assertThatThrownBy(
                () -> otherAccessTokenProvider.validateAuthHeader(authorizationHeader)
        ).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 만료되었습니다.");
    }

    @Test
    void 유효한_엑세스_토큰을_검증한다() {
        final String accessToken = accessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;
        assertThatNoException().
                isThrownBy(() -> accessTokenProvider.validateAuthHeader(authorizationHeader));
    }
}
