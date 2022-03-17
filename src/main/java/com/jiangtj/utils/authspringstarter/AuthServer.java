package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.function.Function;

/**
 * 2018/9/26.
 */
@Slf4j
public class AuthServer {

    @Resource
    private AuthProperties properties;
    @Resource
    private Environment environment;

    @Nullable
    public <T> T getOption(@Nullable String spec, Function<Options, T> fn) {
        if (spec == null) {
            return fn.apply(properties.getDef());
        }
        Options options = properties.getSpec().get(spec);
        if (options == null) {
            return fn.apply(properties.getDef());
        }
        T t = fn.apply(options);
        if (t == null) {
            return fn.apply(properties.getDef());
        }
        return t;
    }

    public SecretKey getKey() {
        return getKey(null);
    }

    public SecretKey getKey(@Nullable String spec){
        String secret = getOption(spec, Options::getSecret);
        if (secret == null) {
            log.warn("你未设置Key，需要设置auth.def.secret");
            throw new KeyException("Unknown key!");
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public Environment getEnvironment() {
        return environment;
    }

    public JWTVerifier verifier() {
        return verifier(null);
    }

    public JWTVerifier verifier(@Nullable String spec) {
        return new JWTVerifier(this, spec);
    }

    public JWTBuilder builder() {
        return builder(null);
    }

    public JWTBuilder builder(@Nullable String spec) {
        return new JWTBuilder(this, spec);
    }
}
