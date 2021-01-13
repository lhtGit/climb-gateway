package com.climb.gateway.constant;

/**
 * @author lht
 * @date 2020/9/16 09:07
 */
public interface GatewayConstant {
    /**
     * 请求request headers 携带token的key
     */
    String AUTHORITY_KEY = "Authority";


    /**
     * 用户
     */
    String USER_INFO = "user:info:";
    /**
     * 用户信息详情
     */
    String USER_INFO_DETAILS = ":details";
    /**
     * 用户基本信息
     */
    String USER_INFO_BASE= ":base";
}
