package com.climb.gateway;

import com.climb.common.handler.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude={
         GlobalExceptionHandler.class
})
//@EnableSwaggerProvider
public class ClimbGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClimbGatewayApplication.class, args);
    }

}
