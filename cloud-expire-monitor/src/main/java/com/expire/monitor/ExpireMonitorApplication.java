package com.expire.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author zhangchun
 */
@SpringBootApplication
@EnableEurekaClient
public class ExpireMonitorApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExpireMonitorApplication.class, args);

    }
}
