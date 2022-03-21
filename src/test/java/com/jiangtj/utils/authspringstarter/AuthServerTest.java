package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created At 2021/3/30.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AuthConfiguration.class)
@TestPropertySource(locations = "classpath:application-auth.properties")
class AuthServerTest {

    @Resource
    private AuthServer authServer;

    @Test
    void testGetOptions() {
        Duration d1 = authServer.getOption(Options::getExpires);
        assertEquals(Duration.ofMinutes(5), d1);
        Duration e1 = authServer.getOption("user", Options::getExpires);
        assertEquals(Duration.ofDays(1), e1);
        Duration e2 = authServer.getOption("un-exists-key", Options::getExpires);
        assertEquals(Duration.ofMinutes(5), e2);
        Duration e3 = authServer.getOption("no-set-name", Options::getExpires);
        assertEquals(Duration.ofMinutes(5), e3);
    }

    @Test
    void testGetKey() {
        SecretKey k1 = authServer.getKey();
        SecretKey k2 = authServer.getKey("no-set-name");
        SecretKey k3 = authServer.getKey("user");
        SecretKey k4 = authServer.getKey("un-exists-key");
        assertEquals(k1, k2);
        assertEquals(k1, k4);
        assertNotEquals(k1, k3);
    }

    @Test
    void testBuilder() {
        String token = authServer.builder("user").build();
        log.error(token);
    }

    @Test
    void testVerifier() {
        authServer.verifier("user");
    }

    @Test
    void testVerifyTime() {
        JWTVerifier verifier = authServer.verifier();
        String token = authServer.builder().build();
        Jws<Claims> verify = verifier.verify(token);
        assertNotNull(verify);

        String expiredToken = authServer.builder()
                .setExpires(Duration.ofDays(100))
                .build();
        assertThrows(ExpiredJwtException.class, () -> {
            verifier.verify(expiredToken);
        });

        assertThrows(ExpiredJwtException.class, () -> {
            authServer.verifier("user").verify("Bearer eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhdXRoIiwiaWF0IjoxNjQ3NDg1MjQ4LCJhdWQiOiIqIiwiZXhwIjoxNjQ3NDg1MjQ5fQ.zLyz2oY2t5GEl5MwsfFUu4R_decWxJXO7pLlfSm6VAI");
        });
    }

    @Test
    void testBuildExtend() {
        String token = authServer.builder("user")
                .setExtend(builder -> {
                    Claims claims = Jwts.claims();
                    claims.put("role", "1,2");
                    builder.addClaims(claims);
                    return builder;
                })
                .build();
        Jws<Claims> user = authServer.verifier("user").verify(token);
        Object role = user.getBody().get("role");
        assertEquals("1,2", role);
    }
}