package com.climb.gateway.login;

import com.climb.common.user.UserBaseInfo;

import java.util.Collection;

/**
 * 用户详情
 * @author lht
 * @since 2020/12/25 13:37
 */
public interface UserDetails {
    /**
     * 用户基本信息
     * @author lht
     * @since  2020/12/25 14:00
     */
    UserBaseInfo getUserBaseInfo();

    /**
     * 用户权限标识符
     * @author lht
     * @since  2020/12/25 14:00
     */
    Collection<String> getAuthoritys();

}
