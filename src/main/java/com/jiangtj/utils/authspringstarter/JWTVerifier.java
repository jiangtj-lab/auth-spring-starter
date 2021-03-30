package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Created At 2021/3/30.
 */
@Slf4j
public class JWTVerifier {

    private final Options options;

    public JWTVerifier(Options options) {
        this.options = options;
    }

    public Jws<Claims> verify(String token) {
        token = verifyRequest(token);
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(options.getKey())
                .build();
        Jws<Claims> claims = parser.parseClaimsJws(token);
        verifyTime(claims.getBody());
        return claims;
    }

    public String verifyRequest(String token) {
        if (!StringUtils.hasLength(token)) {
            log.warn("Token is empty!");
            throw new UnsupportedJwtException("Token is empty!");
        }
        String prefix = options.getRequest().getHeaderPrefix();
        if (!token.startsWith(prefix)) {
            log.warn("Don't have prefix {}!", prefix);
            throw new UnsupportedJwtException("Unsupported authu jwt token!");
        }
        return token.substring(prefix.length());
    }

    public void verifyTime(Claims body) {
        Date issuedAt = body.getIssuedAt();
        Date expiration = body.getExpiration();
        if (issuedAt == null || expiration == null) {
            log.warn("IssuedAt or Expiration is empty!");
            throw new RequiredTypeException("IssuedAt or Expiration is empty!");
        }
        if (expiration.toInstant().isBefore(Instant.now())) {
            log.warn("Token is expired!");
            throw new ExpiredJwtException(null, body, "Token is expired!");
        }
        Duration timeout = Duration.between(issuedAt.toInstant(), expiration.toInstant());
        if (timeout.compareTo(options.getMaxExpires()) > 0) {
            log.warn("Timeout is bigger than max expires time!");
            throw new ExpiredJwtException(null, body, "ExpiresTime is bigger than max expires time!");
        }
    }
}
