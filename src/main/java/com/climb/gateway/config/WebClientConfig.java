package com.climb.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * WebClient 配置
 * @author lht
 * @since 2020/12/18 14:42
 */
@Configuration
public class WebClientConfig {

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * WebClient加入负载均衡
     * @author lht
     * @since  2020/12/25 14:48
     * @param exchangeFilterFunction
     */
    @Bean
    public WebClient webClient(ReactorLoadBalancerExchangeFilterFunction exchangeFilterFunction){
        CustomizeReactorLoadBalancerExchangeFilterFunction function = new CustomizeReactorLoadBalancerExchangeFilterFunction(exchangeFilterFunction);
        return WebClient.builder()
                .filter(function)
                //设置webclient 序列化
                .codecs(configurer -> {
                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
                .build();
    }


    /**
     * 处理webclient使用ip+port访问通过{@link ReactorLoadBalancerExchangeFilterFunction}filter导致找不到服务的异常
     * 当存在端口时，默认为使用ip+port访问，不在进入{@link ReactorLoadBalancerExchangeFilterFunction}
     * @author lht
     * @since  2020/12/28 9:47
     */
    static class CustomizeReactorLoadBalancerExchangeFilterFunction implements ExchangeFilterFunction {
        private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

        public CustomizeReactorLoadBalancerExchangeFilterFunction(ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {
            this.reactorLoadBalancerExchangeFilterFunction = reactorLoadBalancerExchangeFilterFunction;
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
            URI originalUrl = request.url();
            int port = originalUrl.getPort();
            //使用了端口说明不是使用的注册中心
            if(port>0){
                return next.exchange(request);
            }else{
                return reactorLoadBalancerExchangeFilterFunction.filter(request,next);
            }

        }

    }

}
