package com.tag.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.RequiredTypeException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {

    public static final String TOKEN_TYPE = "Bearer";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String CLAIM_NAME = "memberId";

    private final AuthTokenExtractor authTokenExtractor;
    private final Key secretKey;
    private final long expireLength;
    private final JwtParser jwtParser;

    public AccessTokenProvider(final AuthTokenExtractor authTokenExtractor,
                               @Value("${jwt.secret-key}") final String secretKey,
                               @Value("${jwt.expire-length}") final long expireLength) {
        this.authTokenExtractor = authTokenExtractor;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.expireLength = expireLength;
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build();
    }

    public String issueToken(final long memberId) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .setIssuedAt(now)
                .setExpiration(validity)
                .claim(CLAIM_NAME, memberId)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public Long getMemberId(final String authorizationHeader) {
        final String accessToken = authTokenExtractor.extractToken(authorizationHeader, TOKEN_TYPE);
        final Claims body = getBody(accessToken);
        try {
            final Long memberId = body.get(CLAIM_NAME, Long.class);
            if (memberId == null) {
                throw new RuntimeException("토큰 값이 유효하지 않아 추출할 수 없습니다.");
            }
            return memberId;
        } catch (final RequiredTypeException | NullPointerException e) {
            throw new RuntimeException("토큰 값이 유효하지 않아 추출할 수 없습니다.");
        }
    }

    private Claims getBody(final String accessToken) {
        try {
            return jwtParser.parseClaimsJws(accessToken)
                    .getBody();
        } catch (final UnsupportedJwtException | MalformedJwtException | SignatureException |
                       IllegalArgumentException e) {
            throw new RuntimeException("토큰 값이 유효하지 않아 추출할 수 없습니다.");
        } catch (final ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }
    }

    public void validateAuthHeader(final String authorizationHeader) {
        final String accessToken = authTokenExtractor.extractToken(authorizationHeader, TOKEN_TYPE);
        try {
            jwtParser.parse(accessToken);
        } catch (final MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        } catch (final ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        }
    }
}
