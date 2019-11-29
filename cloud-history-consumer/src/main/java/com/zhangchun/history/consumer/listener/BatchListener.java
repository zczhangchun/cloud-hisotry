package com.zhangchun.history.consumer.listener;

import com.xm4399.thrift.util.ThriftUtils;
import com.zhangchun.history.consumer.dao.HistoryDao;
import com.zhangchun.history.consumer.model.GeneralHistoryLogOrigin;
import com.zhangchun.history.consumer.model.History;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zhangchun
 */
@Component
@Slf4j
public class BatchListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HistoryDao historyDao;

    @Autowired
    private DefaultRedisScript script;

    private static final String KEY_FORMAT = "%s-%s-%s";

    private static final String VALUE_FORMAT = "%s-%s-%s";


    // 手动提交ack
    @KafkaListener(id = "12312", topics = "history", containerFactory = "batchContainerFactory", clientIdPrefix = "history")
    public void listener(List<String> dataList, Acknowledgment ack) {

        //接收数据，查缓存有没有该key，不管缓存有没有值，都需要先更新数据库，更新完数据库在更新redis
        List<History> historyList = new ArrayList<>();
        Set<String> keySet = new HashSet<>();
        for (String date : dataList) {
            try {
                GeneralHistoryLogOrigin generalHistoryLogOrigin = ThriftUtils.parseBinaryToObject(date, GeneralHistoryLogOrigin.class);
                String key = generalHistoryLogOrigin.getUserType() + ":" +
                        generalHistoryLogOrigin.getUserId() + ":" +
                        generalHistoryLogOrigin.getItemType() + ":" +
                        generalHistoryLogOrigin.getItemId();
                //判断消费的数据有没有重复
                if (!keySet.contains(key)) {
                    //如果不存在，才加入集合
                    historyList.add(History.builder()
                            .userId(generalHistoryLogOrigin.getUserId())
                            .userType(generalHistoryLogOrigin.getUserType().getValue())
                            .itemType(generalHistoryLogOrigin.getItemType().getValue())
                            .itemId(Integer.valueOf(generalHistoryLogOrigin.getItemId()))
                            .firstTime(generalHistoryLogOrigin.getTimestamp())
                            .lastTime(generalHistoryLogOrigin.getTimestamp())
                            .count(1l)
                            .build());
                }
            } catch (IllegalAccessException e) {
                log.error("数据有误：data=" + date + "错误：" + e.getMessage());
            } catch (InstantiationException e) {
                log.error("数据有误：data=" + date + "错误：" + e.getMessage());
            } catch (TException e) {
                log.error("数据有误：data=" + date + "错误：" + e.getMessage());
            }
        }

        //得到集合后，存入db
        this.historyDao.insertList(historyList);
        log.info("storeDB success : " + historyList);

        //存入Redis
        String key = null;

        List<String> list = null;
        Random random = new Random();
        for (History history : historyList) {
            key =String.format(KEY_FORMAT, history.getUserType(), history.getUserId(), history.getItemType());
            list.add(key);
            list.add(history.getItemId().toString());
            //使用lua脚本原子性插入hash数据
            redisTemplate.execute(script, list, String.format(VALUE_FORMAT, history.getFirstTime(), history.getLastTime(), history.getCount()),(random.nextInt(10) + 86400) + "");
            list.clear();
            key = null;
            log.info("storeCache success : " + history);
        }

        ack.acknowledge();

    }





}
