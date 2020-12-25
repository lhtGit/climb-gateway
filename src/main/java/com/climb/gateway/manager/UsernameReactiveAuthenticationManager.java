package com.climb.gateway.manager;

import com.climb.common.user.auth.LoginUserInfo;
import com.climb.common.user.auth.UserLoginType;
import com.climb.gateway.login.service.LoginService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 登录AuthenticationManager
 * @author lht
 * @date 2020/9/18 16:58
 */
@Component
public class UsernameReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    @Resource
    private LoginService loginService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String principal = StringUtils.isEmpty(authentication.getPrincipal())?"":authentication.getPrincipal().toString();
        String credentials = StringUtils.isEmpty(authentication.getCredentials())?"":authentication.getCredentials().toString();
        UserLoginType userLoginType = UserLoginType.USERNAME_PASSOWRD;
        return loginService.loginAndGetToken(Mono.just(new LoginUserInfo(principal,credentials,userLoginType)));
    }


}
