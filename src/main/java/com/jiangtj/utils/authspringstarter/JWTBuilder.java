package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

/**
 * Created At 2021/3/30.
 */
public class JWTBuilder {

    private final Options options;
    private final Environment environment;
    private String issuer;
    private String subject;
    private String audience = "*";
    private Duration expires;
    private Function<JwtBuilder, JwtBuilder> extend;

    public JWTBuilder(Options options, Environment environment) {
        this.options = options;
        this.environment = environment;
        this.issuer = getApplicationName();
        this.expires = options.getExpires();
    }

    public String build() {
        JwtBuilder builder = Jwts.builder()
                .signWith(options.getKey())
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setAudience(audience)
                .setExpiration(Date.from(Instant.now().plusSeconds(expires.getSeconds())));

        if (extend != null) {
            builder = extend.apply(builder);
        }

        String compact = builder.compact();
        return options.getRequest().getHeaderPrefix() + compact;
    }

    private String getApplicationName() {
        String applicationName = environment.getProperty("spring.application.name");
        if (applicationName != null) {
            return applicationName.toLowerCase();
        }
        return "auth";
    }

    public JWTBuilder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public JWTBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public JWTBuilder setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public JWTBuilder setExtend(Function<JwtBuilder, JwtBuilder> extend) {
        this.extend = extend;
        return this;
    }

    public JWTBuilder setExpires(Duration expires) {
        this.expires = expires;
        return this;
    }
}
