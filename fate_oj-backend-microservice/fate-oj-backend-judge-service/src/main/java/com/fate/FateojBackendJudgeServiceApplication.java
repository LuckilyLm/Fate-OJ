package com.fate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author: Fate
 * @Date: 2024/7/6 19:27
 **/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.fate.feignclient")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class FateojBackendJudgeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FateojBackendJudgeServiceApplication.class, args);
    }
}
