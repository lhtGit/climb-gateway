package com.climb.gateway.handler;

import com.climb.common.util.ResultUtil;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.util.Util;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * 未登录没有权限
 * @Author lht
 * @Date  2020/9/15 15:09
 */
@Component
public class MyServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return Util.result(exchange.getResponse(), ResultUtil.error(ErrorCode.NO_LOGIN_AUTH));
    }
}
