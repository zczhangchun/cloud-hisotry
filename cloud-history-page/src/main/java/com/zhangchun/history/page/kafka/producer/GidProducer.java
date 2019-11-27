package com.zhangchun.history.page.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * @author zhangchun
 */
@Component
@Slf4j
public class GidProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;



    public void sendMsg(List<Long> ids){

        Random random = new Random();

        //模拟生产历史记录

        for (Long id : ids) {

            kafkaTemplate.send("expire", id);
            log.info("send message success ：" + id);
        }



    }

}
