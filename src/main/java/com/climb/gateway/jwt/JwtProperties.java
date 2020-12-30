package com.climb.gateway.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lht
 * @since 2020/12/30 13:52
 */
@Data
@ConfigurationProperties("jwt")
@Configuration
public class JwtProperties {

    /**
     * 秘钥
     */
    private String secret = "1234567asdfghjk";
    /**
     * 过期时间 毫秒 10天
     */
    private Long accessExpiration = 10*24*60*60*1000L;
    /**
     * 刷新过期时间 100天
     */
    private Long refreshExpiration = 100*24*60*60*1000L;
    /**
     * 发行人
     */
    private String issuer = "climb";
}
