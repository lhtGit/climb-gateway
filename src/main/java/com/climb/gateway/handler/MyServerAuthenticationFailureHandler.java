package com.climb.gateway.handler;

import com.climb.gateway.util.Util;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 捕捉抛出 {@link AuthenticationException }下的异常信息，常用在登录位置
 *
 * @author lht
 * @date  2020/9/15 15:59
 */
@Component
public class MyServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {
    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        return Util.result(webFilterExchange.getExchange().getResponse(),exception.getMessage());
    }
}
