package com.fate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author: Fate
 * @Date: 2024/7/6 19:06
 **/

@SpringBootApplication
@MapperScan("com.fate.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.fate.feignclient")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class FateojBackendQuestionServiceApplication
{
    public static void main(String[] args) {
        SpringApplication.run(FateojBackendQuestionServiceApplication.class, args);
    }
}
