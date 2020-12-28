package com.climb.gateway.login;

import com.climb.common.user.auth.LoginUserInfo;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * @author lht
 * @since 2020/12/25 12:29
 */
public interface LoginService {
    /**
     * 用户登录并生成token
     * @author lht
     * @since  2020/12/25 13:32
     * @param userInfo
     */
    Mono<Authentication>  loginAndGetToken(Mono<LoginUserInfo> userInfo);
}
