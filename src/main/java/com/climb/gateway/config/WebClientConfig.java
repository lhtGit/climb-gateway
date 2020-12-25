package com.climb.gateway.config;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

/**
 * WebClient 配置
 * @author lht
 * @since 2020/12/18 14:42
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient加入负载均衡
     * @author lht
     * @since  2020/12/25 14:48
     * @param loadBalancerExchangeFilterFunction
     */
    @Bean
    public WebClient webClient(ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction){
        return WebClient.builder()
                .filter(loadBalancerExchangeFilterFunction)
                .build();
    }



}
