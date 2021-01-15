package com.climb.gateway.controller;

import com.climb.common.exception.GlobalException;
import com.climb.gateway.bean.JwtUser;
import com.climb.gateway.bean.TokenGroup;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 刷新token controller
 * @author lht
 * @since 2021/1/15 11:08
 */
@RestController
public class AuthenticationController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/refresh/token")
    public TokenGroup refreshToken(@RequestBody TokenGroup refreshToken){
        JwtUser jwtUser =  jwtTokenUtil.getSubject(refreshToken.getRefreshToken());
        if(!jwtUser.getIsRefreshToken()){
            throw new GlobalException(ErrorCode.TOKEN_REFRESH_ILLEGAL);
        }
        return jwtTokenUtil.generateToken(jwtUser.getUserFlag());
    }
}
