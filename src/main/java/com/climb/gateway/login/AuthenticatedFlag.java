package com.climb.gateway.login;

/**
 * 用户登录后 用户标识
 * @author lht
 * @since 2020/12/28 11:09
 */
public interface AuthenticatedFlag {

    /**
     * 获取用户唯一标识符
     * @author lht
     * @since  2020/12/28 11:10
     */
    String getFlag();
}
