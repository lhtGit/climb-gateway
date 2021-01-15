package com.climb.gateway.login.form.parse;

import com.climb.common.user.auth.UserLoginType;
import com.climb.gateway.login.authentication.UserAuthentication;
import com.climb.gateway.login.form.ParseForm;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/**
 * 实现用户名密码方式解析登录表单
 * @author lht
 * @since 2020/12/30 16:10
 */
@Component
public class UsernamePasswordParseFrom implements ParseForm {
    private final String usernameParameter = "username";

    private final String passwordParameter = "password";

    @Override
    public UserAuthentication createAuthentication(MultiValueMap<String, String> data) {
        String username = data.getFirst(this.usernameParameter);
        String password = data.getFirst(this.passwordParameter);

        return new UserAuthentication(username,password);
    }

    @Override
    public UserLoginType type() {
        return UserLoginType.USERNAME_PASSOWRD;
    }
}
