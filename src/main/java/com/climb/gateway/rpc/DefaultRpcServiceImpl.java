package com.climb.gateway.rpc;

import com.climb.common.bean.Result;
import com.climb.common.exception.GlobalException;
import com.climb.common.user.auth.LoginUserInfo;
import com.climb.common.user.bean.UserInfoDetails;
import com.climb.common.user.bean.base.DefaultUserInfoDetails;
import com.climb.common.util.ResultUtil;
import com.climb.gateway.exception.ErrorCode;
import com.climb.gateway.login.bean.AuthorityInfo;
import com.climb.gateway.login.bean.UserDetails;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * gateway访问其他服务默认实现
 * @author lht
 * @since 2020/12/25 14:40
 */
@Component
@Slf4j
public class DefaultRpcServiceImpl implements RpcService {
    @Resource
    private WebClient webClient;

    @Override
    public Mono<UserInfoDetails> login(Mono<LoginUserInfo> userInfo) {
        return  webClient.post()
                .uri(uriBuilder ->
                        uriBuilder.scheme("http")
                                .host("127.0.0.1")
                                .port(8082)
                                .path("/user/test/login")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(Charset.defaultCharset())
                .body(userInfo,LoginUserInfo.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<DefaultUserInfoDetails>>() {})
                //重试1次 指数级增长的时间间隔 因数0.5
                //.retryWhen(Retry.backoff(1, Duration.ofSeconds(5)))
                //逻辑处理
                .map(userDetailsResult -> {
                    checkUser(userDetailsResult);
                    return (UserInfoDetails) userDetailsResult.getData();
                })
                .doOnError(e -> {
                    log.error("登录获取用户信息异常",e);
                });
    }

    @Override
    public Mono<Collection<AuthorityInfo>> getAuthorityAll() {
        return Mono.just(Lists.newArrayList());
//        return webClient.get()
//                .uri(uriBuilder ->
//                        uriBuilder.scheme("http")
//                                .host("127.0.0.1")
//                                .port(8082)
//                                .path("/user/test/auth/all")
//                                .build()
//                )
//                .accept(MediaType.APPLICATION_JSON)
//                .acceptCharset(Charset.defaultCharset())
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Result<AuthorityInfo>>(){})
//                .flatMap(result -> {
//                    if(!result.isSuccess()){
//                        return Mono.error(new GlobalException(result.getMsg(),result.getCode()));
//                    }
//                    Collection<AuthorityInfo> authorityInfos =  result.getDataList();
//                    return Mono.just(authorityInfos);
//                })
//                .doOnError(e -> {
//                    log.error("获取所有权限异常",e);
//                });
    }



    /**
     * 校验用户服务获取的用户信息
     * @author lht
     * @since  2020/12/25 14:33
     * @param userDetailsResult
     */
    private void checkUser(Result<DefaultUserInfoDetails> userDetailsResult){
        //异常信息
        if(!userDetailsResult.isSuccess()){
            throw new GlobalException(userDetailsResult.getMsg(),userDetailsResult.getCode());
        }
        //用户信息为空
        if(userDetailsResult.getData()==null){
            throw new GlobalException(ErrorCode.USER_ERROR);
        }
        UserInfoDetails userDetails = userDetailsResult.getData();
        if(userDetails.getResourceInfo()==null){
            throw new GlobalException(ErrorCode.USER_AUTHORITYS_IS_NULL);
        }
        if(userDetails.getMenuInfo()==null){
            throw new GlobalException(ErrorCode.USER_MENU_IS_NULL);
        }
    }
}
