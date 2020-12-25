//package com.climb.gateway.config;
//
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
//import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
//import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.ClientRequest;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.ExchangeFunction;
//import reactor.core.publisher.Mono;
//
//import javax.annotation.Resource;
//import java.net.URI;
//
///**
// * @author lht
// * @since 2020/12/25 17:07
// */
//public class CustomizeReactorLoadBalancerExchangeFilterFunction implements ExchangeFilterFunction {
//
//    @Resource
//    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;
//
//    private final ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory;
//
//    public CustomizeReactorLoadBalancerExchangeFilterFunction(
//            ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory) {
//        this.loadBalancerFactory = loadBalancerFactory;
//    }
//
//    @Override
//    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
//        URI originalUrl = request.url();
//        String serviceId = originalUrl.getHost();
//        ReactiveLoadBalancer<ServiceInstance> loadBalancer = loadBalancerFactory
//                .getInstance(serviceId);
//        if(loadBalancer==null){
//
//        }
//
//        reactorLoadBalancerExchangeFilterFunction.filter(request,next);
//        return null;
//    }
//
//    private ClientRequest buildClientRequest(ClientRequest request, URI uri) {
//        return ClientRequest.create(request.method(), uri)
//                .headers(headers -> headers.addAll(request.headers()))
//                .cookies(cookies -> cookies.addAll(request.cookies()))
//                .attributes(attributes -> attributes.putAll(request.attributes()))
//                .body(request.body()).build();
//    }
//
//}
