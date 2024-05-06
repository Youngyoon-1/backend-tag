package com.tag.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class AccessTokenProvider {

    public static final String TOKEN_TYPE = "Bearer";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String CLAIM_NAME = "memberId";

    private static final String ONE_BLANK_STRING = " ";
    private static final int VALID_COUNT_SPLIT_AUTH_HEADER = 2;
    private static final int FIRST_INDEX_SPLIT_AUTH_HEADER = 0;
    private static final int SECOND_INDEX_SPLIT_AUTH_HEADER = 1;

    private static final String TOKEN_HAS_NO_MEMBER_ID = "토큰에 회원 아이디가 존재하지 않습니다.";
    private static final String FAIL_EXTRACT_TOKEN_AUTH_HEADER_INVALID_TYPE = "토큰의 형식이 유효하지 않아 토큰을 추출할 수 없습니다.";

    private final Key secretKey;
    private final long expireLength;
    private final JwtParser jwtParser;

    public AccessTokenProvider(@Value("${jwt.secret-key}") final String secretKey,
                               @Value("${jwt.expire-length}") final long expireLength) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
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

    public long getMemberId(final String authorizationHeader) {
        final String accessToken = extractToken(authorizationHeader);
        final Claims body = jwtParser.parseClaimsJws(accessToken)
                .getBody();
        final Long memberId = body.get(CLAIM_NAME, Long.class);
        Objects.requireNonNull(memberId, TOKEN_HAS_NO_MEMBER_ID);
        return memberId;
    }

    private String extractToken(final String authorizationHeader) {
        final String[] splitHeaders = authorizationHeader.split(ONE_BLANK_STRING);
        if (splitHeaders.length != VALID_COUNT_SPLIT_AUTH_HEADER
                || !splitHeaders[FIRST_INDEX_SPLIT_AUTH_HEADER].equalsIgnoreCase(TOKEN_TYPE)) {
            throw new IllegalArgumentException(FAIL_EXTRACT_TOKEN_AUTH_HEADER_INVALID_TYPE);
        }
        return splitHeaders[SECOND_INDEX_SPLIT_AUTH_HEADER];
    }
}
