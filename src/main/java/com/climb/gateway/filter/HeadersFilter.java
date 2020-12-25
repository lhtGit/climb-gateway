package com.climb.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.climb.common.constant.CommonConstant;
import com.climb.gateway.util.Util;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 将用户信息保存至header中
 * @author lht
 * @date 2020/9/16 11:04
 */
public class HeadersFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null)
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(authentication -> Util.addHeader(exchange, CommonConstant.USER_INFO, JSON.toJSONString(authentication.getPrincipal())))
                .flatMap(chain::filter)
                .switchIfEmpty(chain.filter(exchange));
    }
}
