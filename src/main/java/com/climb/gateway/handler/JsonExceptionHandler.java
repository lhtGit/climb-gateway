package com.climb.gateway.handler;

import com.climb.common.exception.GlobalException;
import com.climb.common.util.ResultUtil;
import com.climb.gateway.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.*;

import java.util.Map;

/**
 * 捕获及处理异常
 * 主要捕获gateway异常信息，只捕获自身产生的异常报错，
 * 例如：访问的微服务没有启动，则gateway自身报错服务未启动，有该类捕获处理
 * @author lht
 * @date  2020/9/22 9:42
 */
@Slf4j
public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {
    public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = super.getError(request);
        //业务异常处理
        if(error instanceof GlobalException){
            log.error(error.getMessage());
            return ResultUtil.error(error.getMessage(),((GlobalException) error).getCode()).toMap();
        }

        //其他未知异常处理
        log.error("服务请求异常：",error);
        Map<String, Object> errorMap = super.getErrorAttributes(request, options.including(ErrorAttributeOptions.Include.MESSAGE));
        //其他特殊异常暂未处理
        int status = (int)errorMap.get("status");
        Map<String,Object> result = null;
        switch (status){
            case 503:
                result = ResultUtil
                        .error(ErrorCode.ERROR_SERVICE_UNAVAILABLE,errorMap.getOrDefault("message","未知"))
                        .toMap();
                break;
            case 404:
                result = ResultUtil
                        .error(ErrorCode.ERROR_404)
                        .toMap();
                break;
            default:
                result = ResultUtil.error(ErrorCode.ERROR_5xx).toMap();
        }
        return result;
    }


    /**
     * 指定响应处理方法为JSON处理的方法
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * 永久返回200，保证前端稳定
     * @param errorAttributes
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 200;
    }
}
