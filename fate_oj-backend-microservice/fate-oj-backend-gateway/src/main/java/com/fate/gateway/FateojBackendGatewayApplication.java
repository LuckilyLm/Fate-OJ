package com.fate.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: Fate
 * @Date: 2024/7/7 1:11
 **/

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class FateojBackendGatewayApplication
{
    public static void main(String[] args) {
        SpringApplication.run(FateojBackendGatewayApplication.class, args);
    }
}
