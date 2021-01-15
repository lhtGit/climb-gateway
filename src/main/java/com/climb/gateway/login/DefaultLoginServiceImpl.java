package com.climb.gateway.login;

import com.climb.common.user.auth.LoginUserInfo;
import com.climb.gateway.login.authentication.UserAuthentication;
import com.climb.gateway.rpc.RpcService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 登录server默认实现
 * @author lht
 * @since 2020/12/25 13:55
 */
@Component
public class DefaultLoginServiceImpl implements LoginService {

    @Resource
    private RpcService rpcService;
    @Override
    public Mono<Authentication> loginAndGetToken(Mono<LoginUserInfo> userInfo) {
        return rpcService.login(userInfo).map(UserAuthentication::new);
    }


}
