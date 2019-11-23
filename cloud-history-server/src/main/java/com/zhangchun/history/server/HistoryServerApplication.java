package com.zhangchun.history.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhangchun
 */

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@MapperScan("com.zhangchun.history.server.dao")
@EnableAsync
public class HistoryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(HistoryServerApplication.class, args);
    }
}
