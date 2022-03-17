package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.KeyException;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.crypto.SecretKey;
import java.time.Duration;

/**
 * Created At 2021/3/30.
 */
@Slf4j
@Data
public class Options {

    private String secret;
    private Duration expires;
    private Duration maxExpires;
    private String headerName;
    private String headerPrefix;

    public SecretKey getKey(){
        if (this.secret == null) {
            log.warn("你未设置Key，需要设置auth.def.secret");
            throw new KeyException("Unknown key!");
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }

    public static Options def(){
        Options options = new Options();
        options.setExpires(Duration.ofMinutes(5));
        options.setMaxExpires(Duration.ofMinutes(30));
        options.setHeaderName("Authorization");
        options.setHeaderPrefix("Bearer ");
        return options;
    }
}
