package com.climb.gateway.handler;

import com.alibaba.fastjson.JSON;
import com.climb.common.util.ResultUtil;
import com.climb.gateway.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 自定义response装饰器
 * 主要用来处理有微服务返回的，状态码为异常的信息，也可以处理正常的，但是当前只处理异常信息
 * 例如：微服务页面返回里404，但是因为是其他微服务报错，不是gateway本身的异常，
 *      所以对gateway来说只是一种特殊的返回值，该类是对所有返回值做处理
 * @author lht
 * @date 2020/9/22 08:54
 */
@Slf4j
public class CommonResponseDecorator extends ServerHttpResponseDecorator {

    private DataBufferFactory bufferFactory ;
    public CommonResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
        this.bufferFactory = delegate.bufferFactory();
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        ServerHttpResponse response= this.getDelegate();
        if (body instanceof Flux&&(response.getStatusCode()==null||response.getStatusCode().isError())) {
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;

            //TODO 目前只处理微服务404状况
            if(response.getStatusCode()!=null&&response.getStatusCode().value()==404){
                return saveLog(fluxBody, JSON.toJSONString(ResultUtil.error(ErrorCode.ERROR_404)));
            }
            return saveLog(fluxBody, JSON.toJSONString(ResultUtil.error(ErrorCode.ERROR_5xx)));

        }
        return super.writeWith(body);
    }

    /**
     * 记录log
     * @author lht
     * @date  2020/9/22 9:28
     * @param fluxBody
     * @param msg
     */
    private Mono<Void> saveLog(Flux<? extends DataBuffer> fluxBody,String msg){
        return super.writeWith(fluxBody.map(dataBuffer -> {
            byte[] content = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(content);
            // 释放掉内存
            DataBufferUtils.release(dataBuffer);
            String errorMsg = new String(content, StandardCharsets.UTF_8);
            log.error("gateway捕获异常信息：{}",errorMsg);

            byte[] resultMsg =msg.getBytes(StandardCharsets.UTF_8);
            //如果不重新设置长度则收不到消息。
            this.getDelegate().getHeaders().setContentLength(resultMsg.length);
            return bufferFactory.wrap(resultMsg);
        }));
    }
}
