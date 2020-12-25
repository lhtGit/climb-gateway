package com.climb.gateway.bean;

import com.climb.common.user.UserBaseInfo;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

/**
 * 返回前端用户信息，包括token
 * @author lht
 * @since 2020/12/25 16:31
 */
@Data
@Builder
public class UserToken {
    private UserBaseInfo userBaseInfo;

    private Collection<String> authorities;

    private TokenGroup tokenGroup;
}
