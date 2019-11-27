package com.expire.monitor.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhangchun
 */
@Component
@Slf4j
public class ExpireProducer {


    @Autowired
    private KafkaTemplate kafkaTemplate;



    public void sendMsg(String key){


        //模拟生产历史记录

        kafkaTemplate.send("expire", key);
        log.info("send message success ：" + key);

    }
}
