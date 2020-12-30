package com.climb.gateway.config;

import com.climb.gateway.filter.HeadersFilter;
import com.climb.gateway.handler.MyServerAccessDeniedHandler;
import com.climb.gateway.handler.MyServerAuthenticationEntryPoint;
import com.climb.gateway.handler.MyServerAuthenticationFailureHandler;
import com.climb.gateway.handler.MyServerAuthenticationSuccessHandler;
import com.climb.gateway.login.bean.AuthorityInfo;
import com.climb.gateway.login.converter.CustomizeServerAuthenticationConverter;
import com.climb.gateway.rpc.RpcService;
import com.climb.gateway.manager.UsernameReactiveAuthenticationManager;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerFormLoginAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/*
 * Security config
 * @Author lht
 * @Date  2020/9/15 09:43
 */
@Configuration
@EnableWebFluxSecurity
public class AuthConfig  {

    private final String LOGIN_PAGE = "/login";
    @Resource
    private UsernameReactiveAuthenticationManager usernameReactiveAuthenticationManager;
    @Resource
    private MyServerAuthenticationSuccessHandler serverAuthenticationSuccessHandler;

    @Resource
    private MyServerAuthenticationEntryPoint myServerAuthenticationEntryPoint;

    @Resource
    private MyServerAccessDeniedHandler myServerAccessDeniedHandler;

    @Resource
    private RedisServerSecurityContextRepository serverSecurityContextRepository;

    @Resource
    private MyServerAuthenticationFailureHandler myServerAuthenticationFailureHandler;

    @Resource
    private CustomizeServerAuthenticationConverter customizeServerAuthenticationConverter;

    @Resource
    private RpcService rpcService;


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //设置自定义认证管理器
        http.authenticationManager(usernameReactiveAuthenticationManager);


        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(usernameReactiveAuthenticationManager);
        authenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,LOGIN_PAGE));
        authenticationFilter.setAuthenticationFailureHandler(myServerAuthenticationFailureHandler);
        authenticationFilter.setServerAuthenticationConverter(customizeServerAuthenticationConverter);
        authenticationFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler);
        authenticationFilter.setSecurityContextRepository(serverSecurityContextRepository);
        http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.FORM_LOGIN);

        //设置自定义保存用户信息（默认是放到session中）
        http.securityContextRepository(serverSecurityContextRepository);

        http.addFilterAfter(new HeadersFilter(),SecurityWebFiltersOrder.AUTHORIZATION);

        //设置权限
        rpcService.getAuthorityAll()
                .map(authorityInfos -> {
                    ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http.authorizeExchange();
                    authorityInfos.forEach(authorityInfo -> authorizeExchangeSpec.pathMatchers(
                            HttpMethod.resolve(authorityInfo.getMethod()),
                            authorityInfo.getPath()).hasAnyAuthority(authorityInfo.getId()));
                    return getPermit();
                })
                .map(authorityInfos -> {
                    ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http.authorizeExchange();
                    authorityInfos.forEach(authorityInfo -> authorizeExchangeSpec.pathMatchers(
                            HttpMethod.resolve(authorityInfo.getMethod()),
                            authorityInfo.getPath()).permitAll());
                    return authorizeExchangeSpec;
                })
                .subscribe(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated());
        //常规设置
        http.authorizeExchange()
                .and().exceptionHandling()
                //处理未授权
                .accessDeniedHandler(myServerAccessDeniedHandler)
                //处理未认证
                .authenticationEntryPoint(myServerAuthenticationEntryPoint)
                .and().csrf().disable();
        return http.build();
    }




    /**
     * 获取全局访问权限
     * @author lht
     * @since  2020/11/25 13:32
     */
    private List<AuthorityInfo> getPermit(){
        return  Lists.newArrayList(
                new AuthorityInfo("","swagger","/doc.html",HttpMethod.GET.name()),
                new AuthorityInfo("","js","/webjars/**",HttpMethod.GET.name()),
                new AuthorityInfo("","swagger-ui","/swagger-resources/**",HttpMethod.GET.name()),
                new AuthorityInfo("","swagger","/swagger-resources",HttpMethod.GET.name()),
                new AuthorityInfo("","swagger-doc-api","/*/*/v2/api-docs",HttpMethod.GET.name()),
                new AuthorityInfo("","ico","/*.ico",HttpMethod.GET.name()),
                new AuthorityInfo("","登录",LOGIN_PAGE,HttpMethod.POST.name()),
                new AuthorityInfo("","刷新token","/refresh/token",HttpMethod.POST.name())
        );

    }
}
