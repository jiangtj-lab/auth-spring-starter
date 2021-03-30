package com.jiangtj.utils.authspringstarter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 2018/9/26.
 */
@Slf4j
public class AuthServer {

    @Resource
    private AuthProperties properties;
    @Resource
    private Environment environment;


    public Options getOptions() {
        return getOptions(null);
    }

    public Options getOptions(String spec) {
        Options def = properties.getDef();
        Options options = Optional.ofNullable(spec)
                .map(s -> properties.getSpec().get(s))
                .orElse(def);
        if (options.getSecret() == null) {
            options.setSecret(def.getSecret());
        }
        return options;
    }

    public JWTVerifier verifier() {
        return verifier(null);
    }

    public JWTVerifier verifier(String spec) {
        Options options = getOptions(spec);
        return new JWTVerifier(options);
    }

    public JWTBuilder builder() {
        return builder(null);
    }

    public JWTBuilder builder(String spec) {
        Options options = getOptions(spec);
        return new JWTBuilder(options, environment);
    }
}
