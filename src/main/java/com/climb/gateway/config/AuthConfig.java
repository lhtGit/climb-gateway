package com.climb.gateway.config;

import com.climb.gateway.filter.HeadersFilter;
import com.climb.gateway.handler.MyServerAccessDeniedHandler;
import com.climb.gateway.handler.MyServerAuthenticationEntryPoint;
import com.climb.gateway.handler.MyServerAuthenticationFailureHandler;
import com.climb.gateway.handler.MyServerAuthenticationSuccessHandler;
import com.climb.gateway.login.bean.AuthorityInfo;
import com.climb.gateway.rpc.RpcService;
import com.climb.gateway.manager.UsernameReactiveAuthenticationManager;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import javax.annotation.Resource;
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
    private RpcService rpcService;


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        //设置自定义认证管理器
        http.authenticationManager(usernameReactiveAuthenticationManager);
        http.formLogin()
                //登录成功handler
                .authenticationSuccessHandler(serverAuthenticationSuccessHandler)
                //设置一下三个配置是为了在浏览器访问login页面时不会跳转到登录页面
                .authenticationEntryPoint(myServerAuthenticationEntryPoint)
                .requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST,LOGIN_PAGE))
                .authenticationFailureHandler(myServerAuthenticationFailureHandler);
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
                .subscribe(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
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

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
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
