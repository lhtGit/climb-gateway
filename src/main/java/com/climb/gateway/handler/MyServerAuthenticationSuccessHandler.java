package com.climb.gateway.handler;

import com.climb.common.util.ResultUtil;
import com.climb.gateway.bean.UserToken;
import com.climb.gateway.login.bean.UserDetails;
import com.climb.gateway.util.JwtTokenUtil;
import com.climb.gateway.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/*
 * 认证成功返回用户信息包括token生成
 * @Author lht
 * @Date  2020/9/15 10:51
 */
@Component
@Slf4j
public class MyServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        //保存token信息
        //返回用户信息
        UserToken loginUserRes = UserToken.builder()
                .userBaseInfo(userDetails.getUserBaseInfo())
                .authorities(userDetails.getAuthoritys())
                .tokenGroup(jwtTokenUtil.generateToken(userDetails.getUserBaseInfo().getId()))
                .build();
        return Util.result(webFilterExchange.getExchange().getResponse(), ResultUtil.success(loginUserRes));
    }
}
