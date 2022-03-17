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
    @Resource
    private AuthProperties properties;

    /*@Test
    void testGetOptions() {
        Options options = authServer.getOptions();
        assertEquals(properties.getDef(), options);
        Options no = authServer.getOptions("noex");
        assertEquals(properties.getDef(), no);
        Options user = authServer.getOptions("user");
        assertEquals(Duration.ofDays(10), user.getMaxExpires());
        assertNotNull(user.getRequest());
    }*/

    @Test
    void testBuilder() {
        authServer.builder("user");
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