package com.climb.gateway.handler;

import com.climb.common.util.ResultUtil;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.util.Util;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
 * 登录后没有权限
 * @Author lht
 * @Date  2020/9/15 15:38
 */
@Component
public class MyServerAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return Util.result(exchange.getResponse(), ResultUtil.error(ErrorCode.NO_AUTH));
    }
}
