package com.climb.gateway.login.bean;

import com.climb.common.user.UserBaseInfo;
import com.climb.gateway.login.AuthenticatedFlag;
import lombok.Data;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.Collection;

/**
 * 用户详情
 * @author lht
 * @since 2020/12/25 13:37
 */
@Data
public class UserDetails implements AuthenticatedPrincipal, AuthenticatedFlag {
    private UserBaseInfo userBaseInfo;


    private Collection<String> authoritys;

    @Override
    public String getName() {
        return userBaseInfo.getUsername();
    }


    @Override
    public String getFlag() {
        return userBaseInfo.getId();
    }
}
