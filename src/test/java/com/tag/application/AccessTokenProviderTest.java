package com.tag.application;

import static com.tag.application.auth.AccessTokenProvider.TOKEN_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.application.auth.AccessTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AccessTokenProviderTest {

    private static AccessTokenProvider accessTokenProvider;
    private static Key secretKey;

    @BeforeAll
    static void beforeAll() {
        accessTokenProvider = new AccessTokenProvider("secretKeySecretKeySecretKeySecretKeySecretKey", 1000);
        secretKey = Keys.hmacShaKeyFor("secretKeySecretKeySecretKeySecretKeySecretKey".getBytes());
    }

    @Test
    void 엑세스_토큰을_발급한다() {
        // when
        final String accessToken = accessTokenProvider.issueToken(10L);

        // then
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
    void 인가_헤더에서_멤버_아이디를_추출해서_반환한다() {
        // given
        final String accessToken = accessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;

        // when
        final long memberId = accessTokenProvider.getMemberId(authorizationHeader);

        // then
        assertThat(memberId).isEqualTo(10L);
    }

    @Test
    void 인가_헤더에서_멤버_아이디를_추출해서_반환한다_토큰_형식이_유효하지_않으면_예외가_발생한다() {
        // given
        final String authorizationHeader = TOKEN_TYPE + " " + "invalidToken";

        // when, then
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(authorizationHeader)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("JWT strings must contain exactly 2 period characters. Found: 0");
    }

    @Test
    void 인가_헤더에서_멤버_아이디를_추출해서_반환한다_만료된_토큰인_경우_예외가_발생한다() {
        // given
        final AccessTokenProvider otherAccessTokenProvider = new AccessTokenProvider(
                "otherSecretKeyOtherSecretKeyOtherSecretKeyOtherSecretKey", 0);
        final String accessToken = otherAccessTokenProvider.issueToken(10L);
        final String authorizationHeader = TOKEN_TYPE + " " + accessToken;

        // when, when
        assertThatThrownBy(
                () -> otherAccessTokenProvider.getMemberId(authorizationHeader)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void 인가_헤더에서_회원_아이디를_추출할때_토큰안에_있는_member_id_가_null_이면_예외가_발생한다() {
        // given
        final Date now = new Date();
        final String accessToken = Jwts.builder()
                .setSubject("AccessToken")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 10000))
                .claim("memberId", null)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        final String invalidToken = TOKEN_TYPE + " " + accessToken;

        // when, then
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(invalidToken)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("토큰에 회원 아이디가 존재하지 않습니다.");
    }

    @Test
    void 인가_헤더에서_회원_아이디를_추출할때_회원_아이디를_담은_claim_타입이_일치하지_않으면_예외가_발생한다() {
        // given
        final Date now = new Date();
        final String accessToken = Jwts.builder()
                .setSubject("AccessToken")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 10000))
                .claim("memberId", "invalidType")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        final String invalidToken = TOKEN_TYPE + " " + accessToken;

        // when, then
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(invalidToken)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot convert existing");
    }

    @Test
    void 인가_헤더에서_회원_아이디를_추출할때_인가_헤더가_null_인_경우_예외가_발생한다() {
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(null)
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("null");
    }

    @Test
    void 인가_헤더에서_회원_아이디를_추출할때_인가_헤더의_형식이_유효하지_않은_경우_예외가_발생한다() {
        // given
        final String httpAuthorizationHeader = TOKEN_TYPE + " " + "token Value";

        // when, then
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(httpAuthorizationHeader)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("토큰의 형식이 유효하지 않아 토큰을 추출할 수 없습니다.");
    }

    @Test
    void 인가_헤더에서_회원_아이디를_추출할때_토큰_타입이_유효하지_않은_경우_예외가_발생한다() {
        // given
        final String httpAuthorizationHeader = "INVALID_TOKEN_TYPE tokenValue";

        // when, then
        assertThatThrownBy(
                () -> accessTokenProvider.getMemberId(httpAuthorizationHeader)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("토큰의 형식이 유효하지 않아 토큰을 추출할 수 없습니다.");
    }
}
