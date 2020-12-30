package com.climb.gateway.login.converter;

import com.climb.gateway.login.form.ParseFromContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 自定义解析登录表单
 * @author lht
 * @since 2020/12/30 15:51
 */
@Component
public class CustomizeServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Resource
    private ParseFromContext parseFromContext;


    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return  exchange.getFormData()
                .map(parseFromContext::parse);
    }


}
