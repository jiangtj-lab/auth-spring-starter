package com.jiangtj.utils.authspringstarter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * 2018/9/26.
 */
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    @NestedConfigurationProperty
    private Options def = new Options();
    private Map<String, Options> spec;
}
