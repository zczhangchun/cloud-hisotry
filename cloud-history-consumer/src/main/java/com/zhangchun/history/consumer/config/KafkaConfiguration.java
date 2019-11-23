package com.zhangchun.history.consumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个服务只需要消费数据，所以不配置生产者
 * 使用批处理消费数据
 * @author zhangchun
 */
@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Bean("batchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory listenerContainer() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(5);
        //设置为批量监听
        container.setBatchListener(true);


        container.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);

        return container;
    }


    @Bean
    public NewTopic batchTopic() {
        return new NewTopic("history", 8, (short) 1);
    }

    //消费者配置
    private Map<String, Object> consumerProps(){

        Map<String, Object> props = new HashMap<String, Object>();

        //连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafkaIP:9092");
        //GroupID
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "history");
        //关闭自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        //Session超时设置
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //键的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        //值的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}
