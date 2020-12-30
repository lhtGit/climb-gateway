package com.climb.gateway.exception;


import com.climb.common.exception.ErrorMessage;

/**
 * @author lht
 * @date 2020/9/21 10:52
 */
public enum ErrorCode implements ErrorMessage {
    //gateway校验token
    TOKEN_TIMEOUT(3001,"token已过期，请重新登录！"),
    TOKEN_PARSING_ERROR(3002,"token解析失败"),
    NO_LOGIN_AUTH(3003,"请登录后重试！"),
    NO_AUTH(3005,"没有权限访问，请联系管理员"),

    //gateway统一异常返回
    ERROR_404(404,"找不到页面"),
    ERROR_5xx(500,"服务器请求异常"),
    ERROR_SERVICE_UNAVAILABLE(503,"服务未启动"),

    //token 非法
    TOKEN_ACCESS_ILLEGAL(3010,"非法访问,请使用认证token！"),
    TOKEN_REFRESH_ILLEGAL(3011,"非法访问,请使用刷新token！"),

    //user
    USER_ERROR(3020,"用户登录失败"),
    USER_INFO_IS_NULL(3021,"用户信息不能为null"),
    USER_AUTHORITYS_IS_NULL(3022,"用户权限不能为null"),
    //rsa
    RSA_GENERATE_ERROR(3030,"RAS生成失败"),
    //parse login form
    PARSE_LOGIN_TYPE(3040,"解析获取登录类型失败"),

    PARSE_LOGIN_TYPE_NOT_FIND(3041,"登录类型不存在"),
    ;

    private final int code;
    private final String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
