package com.climb.gateway.login.service;

import com.climb.common.user.auth.LoginUserInfo;
import com.climb.gateway.login.UserDetails;
import com.climb.gateway.login.authority.AuthorityInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * gateway访问其他服务
 * 因为gateway使用feign会出现request不兼容问题，所以推荐使用{@link WebClient}
 * 现在项目中已经配置了{@link WebClient}在{@link com.climb.gateway.config.WebClientConfig}
 * @author lht
 * @since 2020/12/25 13:57
 */
public interface RpcService {
    /**
     * 登录并获取用户信息
     * @author lht
     * @since  2020/12/25 14:35
     * @param userInfo
     */
    Mono<UserDetails> login(Mono<LoginUserInfo> userInfo);

    /**
     * 获取所有权限
     * @author lht
     * @since  2020/12/25 16:47
     */
    Mono<Collection<AuthorityInfo>> getAuthorityAll();
}
