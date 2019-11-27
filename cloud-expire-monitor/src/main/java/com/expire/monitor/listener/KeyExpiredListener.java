package com.expire.monitor.listener;

import com.expire.monitor.kafka.producer.ExpireProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.nio.charset.StandardCharsets;

/**
 * @author zhangchun
 */
@Slf4j
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    private ExpireProducer expireProducer;


    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = new String(message.getChannel(), StandardCharsets.UTF_8);
        String key = new String(message.getBody(),StandardCharsets.UTF_8);
        if (key.contains("sign")){

            log.info("redis key 过期：key={}",new String(pattern), msg, key.split("_")[0]);
            expireProducer.sendMsg(key);
        }

    }
}
