package com.climb.gateway.login.bean;

import com.climb.common.user.auth.UserLoginType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public UserAuthentication(Object principal, Object credentials, UserLoginType userLoginType) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.userLoginType = userLoginType;
        // must use super, as we override
        super.setAuthenticated(false);
    }

    public UserAuthentication(Object principal, Collection<SimpleGrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // must use super, as we override
        super.setAuthenticated(true);
    }

    public UserLoginType getUserLoginType() {
        return userLoginType;
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
