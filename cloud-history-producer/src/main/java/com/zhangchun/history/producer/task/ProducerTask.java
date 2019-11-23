package com.zhangchun.history.producer.task;


import com.zhangchun.history.producer.model.HistoryInfo;
import com.zhangchun.history.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

/**
 * @author zhangchun
 */
@EnableScheduling
@Component
@Slf4j
public class ProducerTask {

    @Autowired
    private KafkaTemplate kafkaTemplate;



    @Scheduled(cron="0/1 * * * * ?")
    public void sendMsg(){

        Random random = new Random();

        //模拟生产历史记录
        HistoryInfo historyInfo = HistoryInfo.builder()
                .uid(random.nextInt(10))
                .ip("192.168.0." + random.nextInt(10))
                .gid(random.nextInt(10))
                .firstTime(new Date(System.currentTimeMillis() - 10))
                .lastTime(new Date())
                .count(random.nextInt(10) + 2)
                .itemType(random.nextInt(3) + 1)
                .build();

        String str = JsonUtils.toString(historyInfo);
        kafkaTemplate.send("history", str);
        log.info("send message success ：" + str);

    }
}
