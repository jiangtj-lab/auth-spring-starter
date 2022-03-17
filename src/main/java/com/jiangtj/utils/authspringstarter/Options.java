package com.jiangtj.utils.authspringstarter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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

    public static Options def(){
        Options options = new Options();
        options.setExpires(Duration.ofMinutes(5));
        options.setMaxExpires(Duration.ofMinutes(30));
        options.setHeaderName("Authorization");
        options.setHeaderPrefix("Bearer ");
        return options;
    }
}
