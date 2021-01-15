package com.climb.gateway.login.form;

import com.climb.common.exception.GlobalException;
import com.climb.common.user.auth.UserLoginType;
import com.climb.gateway.login.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析登录表单上下文
 * @author lht
 * @since 2020/12/30 16:14
 */
@Component
public class ParseFromContext {
    private final String typeParameter = "type";

    private final Map<String,ParseForm> PARSE_FORM_MAP = new HashMap<>();

    @Autowired
    public void parseForms(List<ParseForm> parseForms){
        parseForms.forEach(parseForm -> PARSE_FORM_MAP.put(parseForm.type().name(),parseForm));
    }

    /**
     * 解析表单，创建相关Authentication
     * @author lht
     * @since  2020/12/30 16:28
     * @param data
     */
    public UserAuthentication parse(MultiValueMap<String, String> data){
        UserLoginType userLoginType = parseType(data);
        ParseForm parseForm = PARSE_FORM_MAP.get(userLoginType.name());
        if(parseForm == null){
            throw new GlobalException("没有找到登录类型"+userLoginType+"的解析器，请先添加解析器！");
        }
        UserAuthentication userAuthentication  = parseForm.createAuthentication(data);
        userAuthentication.setUserLoginType(parseForm.type());
        return userAuthentication ;
    }



    /**
     * 解析登录类型
     * @author lht
     * @since  2020/12/30 16:30
     * @param data
     */
    public UserLoginType parseType(MultiValueMap<String, String> data){
        String type = data.getFirst(typeParameter);
        if(StringUtils.isEmpty(type)){
            return  UserLoginType.USERNAME_PASSOWRD;
        }
        type = type.toUpperCase();
        try{
            return UserLoginType.valueOf(type);
        }catch (IllegalArgumentException e){
            throw new GlobalException("登录类型["+type+"]不存在");
        }catch (Exception e){
            throw new GlobalException("解析获取登录类型["+type+"]失败");
        }
    }
}
