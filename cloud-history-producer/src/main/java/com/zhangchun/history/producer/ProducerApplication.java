package com.zhangchun.history.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * 用这个服务来模拟生产历史数据
 * @author zhangchun
 */

@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
public class ProducerApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProducerApplication.class, args);

    }
}
