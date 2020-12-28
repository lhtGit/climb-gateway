package com.climb.gateway.login.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限信息
 * @author lht
 * @since 2020/12/25 16:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityInfo {
    /**
     * 权限标识符
     */
    private String id;
    /**
     * 资源名称
     */

    private String name;
    /**
     * url
     */

    private String path;
    /**
     * 请求类型
     */

    private String method;
}
