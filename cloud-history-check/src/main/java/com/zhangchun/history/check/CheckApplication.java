package com.zhangchun.history.check;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author zhangchun
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.zhangchun.history.check.dao")
public class CheckApplication {

    public static void main(String[] args) {

        SpringApplication.run(CheckApplication.class, args);
    }


}
