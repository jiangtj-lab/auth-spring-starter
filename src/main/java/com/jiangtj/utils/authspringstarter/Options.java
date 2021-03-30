package com.jiangtj.utils.authspringstarter;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
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
    private Duration expires = Duration.ofMinutes(5);
    private Duration maxExpires = Duration.ofMinutes(30);
    @NestedConfigurationProperty
    private Request request = new Request();

    @Data
    static public class Request {
        private String headerName = "Authorization";
        private String headerPrefix = "Bearer ";
    }

    public SecretKey getKey(){
        if (this.secret == null) {
            SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("你未设置Key，需要设置auth.def.secret");
            return secretKey;
        }
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }
}
