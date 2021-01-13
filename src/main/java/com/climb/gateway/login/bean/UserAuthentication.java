package com.climb.gateway.login.bean;

import com.climb.common.user.auth.UserLoginType;
import com.google.common.collect.Lists;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import java.util.Collection;

/**
 * An {@link org.springframework.security.core.Authentication} implementation that is
 * designed for simple presentation of a user authentication token.
 * @author lht
 * @since 2020/12/25 14:04
 */
public class UserAuthentication extends AbstractAuthenticationToken {
    private final Object principal;

    private Object credentials;

    private UserLoginType userLoginType;

    /**
     * 用于登录传递用户登录信息使用
     * @author lht
     * @since  2021/1/13 17:41
     * @param principal
     * @param credentials
     */
    public UserAuthentication(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        // must use super, as we override
        super.setAuthenticated(false);
    }

    /**
     * 用户获取用户信息后传递到{@link ServerSecurityContextRepository}保存用户信息
     * @author lht
     * @since  2021/1/13 17:42
     * @param principal
     */
    public UserAuthentication(Object principal) {
        super(Lists.newArrayList());
        this.principal = principal;
        // must use super, as we override
        super.setAuthenticated(true);
    }

    /**
     * 用户使用jwt解析后传递security认证使用
     * @author lht
     * @since  2021/1/13 17:42
     * @param principal
     * @param authorities
     */
    public UserAuthentication(Object principal, Collection<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // must use super, as we override
        super.setAuthenticated(true);
    }

    public UserLoginType getUserLoginType() {
        return userLoginType;
    }

    public void setUserLoginType(UserLoginType userLoginType) {
        this.userLoginType = userLoginType;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
