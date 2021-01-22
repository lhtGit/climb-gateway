package com.climb.gateway.config;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.climb.common.exception.GlobalException;
import com.climb.common.user.bean.UserInfoDetails;
import com.climb.common.user.bean.UserInfoBase;
import com.climb.gateway.bean.JwtUser;
import com.climb.gateway.constant.GatewayConstant;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.authentication.UserAuthentication;
import com.climb.gateway.jwt.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;


/**
 * 使用redis 存在用户信息
 * @author lht
 * @since  2020/11/26 13:52
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class RedisServerSecurityContextRepository implements ServerSecurityContextRepository {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    /**
     * 用户
     */
    private final String USER_INFO = "user:info:";
    /**
     * 用户信息详情
     */
    private final String USER_INFO_DETAILS = ":details";
    /**
     * 用户基本信息
     */
    private final String USER_AUTHENTICATION= ":authentication";

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        log.info("ServerSecurityContextRepository.save:{}",context);
        Authentication authentication = context.getAuthentication();
        //获得用户信息
        UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();


        //保存用户信息详情(user:info:{userid}:details)
        redisTemplate.opsForValue().set(USER_INFO+userInfoDetails.getId()+USER_INFO_DETAILS
                , JSON.toJSONString(userInfoDetails)
        );


        //保存用户基本信息(user:info:{userid}:base)
        UserInfoBase userInfoBase = BeanUtil.copyProperties(userInfoDetails, UserInfoBase.class);
        UserAuthentication userAuthentication = new UserAuthentication(userInfoBase, userInfoDetails.getResourceInfo()
                .stream()
                .map(resourceRes -> new SimpleGrantedAuthority(resourceRes.getId()))
                .collect(Collectors.toList()));

        redisTemplate.opsForValue().set(USER_INFO+userInfoDetails.getId()+USER_AUTHENTICATION
                , JSON.toJSONString(userAuthentication)
        );
        return Mono.empty();

    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(GatewayConstant.AUTHORITY_KEY);
        SecurityContext context = null;
        if(!StringUtils.isEmpty(token)){
            //解析token jwt
            JwtUser userSubject =  jwtTokenUtil.getSubject(token);
            if(userSubject.getIsRefreshToken()){
                throw new GlobalException(ErrorCode.TOKEN_ACCESS_ILLEGAL);
            }
            String userFlag = userSubject.getUserFlag();
            String userInfo = redisTemplate.opsForValue().get(USER_INFO+userFlag+USER_AUTHENTICATION);
            if(!StringUtils.isEmpty(userInfo)){
                UserAuthentication userAuthentication = JSON.parseObject(userInfo,new TypeReference<UserAuthentication>(){});
                //显式设置为已登录 避免因为生成对象时 设置成false
                userAuthentication.setAuthenticated(true);
                context = new SecurityContextImpl(userAuthentication);
            }else{
                //TODO 是否在验证token合法后，自动获得用户信息，因为理论来说是应该存在的（先不写）
            }
        }
        return Mono.justOrEmpty(context);
    }

}
