package com.climb.gateway.rpc;

import com.climb.common.bean.Result;
import com.climb.common.exception.GlobalException;
import com.climb.common.user.auth.LoginUserInfo;
import com.climb.common.user.bean.ResourceInfo;
import com.climb.common.user.bean.UserInfoDetails;
import com.climb.gateway.exception.ErrorCode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
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
public class DefaultRpcServiceImpl implements RpcService, ApplicationContextAware {
    @Resource
    private WebClient webClient;

    private ConfigurableApplicationContext context;

    @Override
    public Mono<UserInfoDetails> login(Mono<LoginUserInfo> userInfo) {
        return  webClient.post()
//                 .uri(uriBuilder ->
//                        uriBuilder.host("gglc-erp-base")
//                                .path("/erp/base/employee/login")
//                                .build()
//                )
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
                .bodyToMono(new ParameterizedTypeReference<Result<UserInfoDetails>>() {})
                //重试1次 指数级增长的时间间隔 因数0.5
                //.retryWhen(Retry.backoff(1, Duration.ofSeconds(5)))
                //逻辑处理
                .map(userDetailsResult -> {
                    checkUser(userDetailsResult);
                    return userDetailsResult.getData();
                })
                .doOnError(e -> {
                    log.error("登录获取用户信息异常",e);
                });
    }


    @Override
    public Mono<Collection<ResourceInfo>> getAuthorityAll() {
//        return Mono.just(Lists.newArrayList());
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("http")
                                .host("127.0.0.1")
                                .port(8082)
                                .path("/user/test/auth/all")
                                .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(Charset.defaultCharset())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Result<ResourceInfo>>(){})
                .flatMap(result -> {
                    if(!result.isSuccess()){
                        return Mono.error(new GlobalException(result.getMsg(),result.getCode()));
                    }
                    Collection<ResourceInfo> authorityInfos =  result.getDataList();
                    return Mono.just(authorityInfos);
                })
                .doOnError(e -> {
                    log.error("获取所有权限异常",e);
                    //停止项目
                    context.close();
                });
    }


    /**
     * 校验用户服务获取的用户信息
     * @author lht
     * @since  2020/12/25 14:33
     * @param userDetailsResult
     */
    private void checkUser(Result<UserInfoDetails> userDetailsResult){
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.context =  (ConfigurableApplicationContext) applicationContext;
        }
    }
}
