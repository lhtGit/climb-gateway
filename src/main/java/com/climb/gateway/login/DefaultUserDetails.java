package com.climb.gateway.login;

import com.climb.common.user.UserBaseInfo;
import lombok.Data;

import java.util.Collection;

/**
 * @author lht
 * @since 2020/12/25 16:42
 */
@Data
public class DefaultUserDetails implements UserDetails {

    private UserBaseInfo userBaseInfo;


    private Collection<String> authoritys;
}
