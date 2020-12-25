package com.climb.gateway.config;

import com.alibaba.fastjson.JSON;
import com.climb.common.exception.GlobalException;
import com.climb.common.user.UserBaseInfo;
import com.climb.gateway.bean.JwtUser;
import com.climb.gateway.constant.GatewayConstant;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.login.UserDetails;
import com.climb.gateway.login.authority.UserAuthenticationToken;
import com.climb.gateway.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 使用redis 存在用户信息
 * @author lht
 * @since  2020/11/26 13:52
 */
@Component
@Slf4j
public class RedisServerSecurityContextRepository implements ServerSecurityContextRepository {
//    @Autowired
//    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        log.info("ServerSecurityContextRepository.save:{}",context);
        Authentication authentication = context.getAuthentication();
        //获得用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserBaseInfo userBaseInfo = userDetails.getUserBaseInfo();
        //保存用户信息
//        redisTemplate.opsForValue().set(GatewayConstant.USER_AUTH_REDIS+userBaseInfo.getId()
//                , JSON.toJSONString(new UserAuthenticationToken(userBaseInfo, authentication.getAuthorities()))
//        );
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
//            String userInfo = redisTemplate.opsForValue().get(GatewayConstant.USER_AUTH_REDIS+userFlag);
//            if(!StringUtils.isEmpty(userInfo)){
//                context = new SecurityContextImpl(JSON.parseObject(userInfo,UserAuthenticationToken.class));
//            }else{
//                //TODO 是否在验证token合法后，自动获得用户信息，因为理论来说是应该存在的（先不写）
//            }
        }
        return Mono.justOrEmpty(context);
    }

}
