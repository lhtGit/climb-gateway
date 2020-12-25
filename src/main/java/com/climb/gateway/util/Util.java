package com.climb.gateway.util;

import com.alibaba.fastjson.JSON;
import com.climb.common.constant.CommonConstant;
import com.climb.gateway.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;

/*
 *
 * @Author lht
 * @Date  2020/9/15 15:40
 */
@Slf4j
public class Util {
    /*
     * 设置返回值
     * @author lht
     * @date  2020/9/16 9:32
     * @param response
     * @param msg
     */
    public static Mono<Void> result(ServerHttpResponse response, Object msg) {
        byte[] datas = JSON.toJSONBytes(msg);
        DataBuffer buffer = response.bufferFactory().wrap(datas);
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    /*
     * 新增header
     * @author lht
     * @date  2020/9/16 9:32
     * @param exchange
     * @param key
     * @param val
     */
    public static ServerWebExchange  addHeader(ServerWebExchange exchange,String key,String val){
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
            try {
                httpHeaders.add(key, URLEncoder.encode(val, CommonConstant.UTF8));
            } catch (Exception e) {
                log.info("添加信息至请求头失败。");
            }
        }).build();
        return exchange.mutate().request(serverHttpRequest).build();
    }

    /**
     * 获得请求中的token
     * @author lht
     * @date  2020/9/21 11:29
     * @param exchange
     */
    public static String getToken(ServerWebExchange exchange){
        if(exchange==null){
            return null;
        }
        return exchange.getRequest().getHeaders().getFirst(GatewayConstant.AUTHORITY_KEY);
    }

    /**
     * 判断请求中的token是否为空
     * @author lht
     * @date  2020/9/21 11:29
     * @param exchange
     */
    public static boolean isNonToken(ServerWebExchange exchange){
        return StringUtils.isEmpty(getToken(exchange));
    }

}
