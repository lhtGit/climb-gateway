package com.climb.gateway.util;

import com.alibaba.fastjson.JSON;
import com.climb.common.exception.GlobalException;
import com.climb.common.util.IdUtils;
import com.climb.gateway.bean.JwtUser;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.bean.TokenGroup;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RefreshScope
public class JwtTokenUtil {
    private String secret="1234567asdfghjk";
    // 过期时间 毫秒 10天
//    @Value("${jwt.access.expiration:1000000}")
    private long accessExpiration = 10*24*60*60*1000;

    /**
     * 刷新过期时间 100天
     */
//    @Value("${jwt.refresh.expiration:1000000}")
    private long refreshExpiration = 100*24*60*60*1000;

    private String issuer="zl";


    /**
     * 生成token
     * @author lht
     * @since  2020/11/26 18:04
     * @param userFlag
     */
    public TokenGroup  generateToken(String userFlag){
        //保存token信息
        String accessToken = generate(
                JSON.toJSONString(new JwtUser(userFlag,false)),false);
        String refreshToken = generate(
                JSON.toJSONString(new JwtUser(userFlag,true)),true);
        return new TokenGroup(accessToken,refreshToken);
    }

    /**
     * 生成jwt
     * @author lht
     * @since  2020/11/26 11:10
     * @param subject
     */
    public String generate(String subject,boolean isRefreshToken){
        Claims claims = new DefaultClaims();
        if(isRefreshToken){
            claims.setExpiration(new Date(System.currentTimeMillis()+refreshExpiration));
        }else{
            claims.setExpiration(new Date(System.currentTimeMillis()+accessExpiration));
        }
        claims.setIssuedAt(new Date());
        claims.setSubject(subject);
        claims.setIssuer(issuer);
        claims.setId(IdUtils.nextId());
        return doGenerateToken(claims);
    }

    /**
     * 解析token并获取subject
     * @author lht
     * @since  2020/11/26 11:13
     * @param token
     */
    public JwtUser getSubject(String token){
        Claims claims = doParserToken(token);
        return JSON.parseObject(claims.getSubject(), JwtUser.class);
    }
    /**
     * 获取jwt
     * @author lht
     * @since  2020/11/26 11:07
     * @param claims
     */
    private String doGenerateToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 解析jwt
     * @author lht
     * @since  2020/11/26 11:07
     * @param token
     */
    private  Claims doParserToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException expiredJwtException){
            throw new GlobalException(ErrorCode.TOKEN_TIMEOUT);
        }catch (Exception e) {
            throw new GlobalException(ErrorCode.TOKEN_PARSING_ERROR);
        }
        return claims;
    }
}
