package com.climb.gateway.bean;

import com.climb.common.user.bean.UserInfoDetails;
import lombok.Builder;
import lombok.Data;

/**
 * 返回前端用户信息，包括token
 * @author lht
 * @since 2020/12/25 16:31
 */
@Data
@Builder
public class UserToken {
    private UserInfoDetails userInfoDetails;


    private TokenGroup tokenGroup;
}
