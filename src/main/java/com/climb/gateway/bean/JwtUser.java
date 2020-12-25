package com.climb.gateway.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 保存在jwt中
 * @author lht
 * @since 2020/11/26 11:37
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtUser implements Serializable {
    private static final long serialVersionUID = -1012200490590861594L;
    /**
     * 用户标识
     * @author lht
     * @since  2020/11/26 11:39
     */
    private  String userFlag;


    /**
     * 是否为刷新token
     * @author lht
     * @since  2020/11/26 12:23
     */
    private Boolean isRefreshToken;

}
