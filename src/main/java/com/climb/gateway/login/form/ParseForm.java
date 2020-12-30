package com.climb.gateway.login.form;

import com.climb.common.user.auth.UserLoginType;
import com.climb.gateway.login.bean.UserAuthentication;
import org.springframework.util.MultiValueMap;

/**
 * 解析登录表单，创建Authentication
 * @author lht
 * @since 2020/12/30 16:07
 */
public interface ParseForm {


    UserAuthentication createAuthentication(MultiValueMap<String, String> data);

    UserLoginType type();
}
