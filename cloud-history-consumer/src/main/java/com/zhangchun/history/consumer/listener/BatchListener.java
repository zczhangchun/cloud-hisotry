package com.zhangchun.history.consumer.listener;

import com.zhangchun.history.consumer.dao.HistoryInfoDao;
import com.zhangchun.history.consumer.model.HistoryInfo;
import com.zhangchun.history.consumer.model.HistoryInfoExample;
import com.zhangchun.history.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangchun
 */
@Component
@Slf4j
public class BatchListener {

    @Autowired
    private HistoryInfoDao historyInfoDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "%s-%s-%s";

    private static final String VALUE_PREFIX = "%s-%s-%s";

    // 手动提交ack
    @KafkaListener(id = "12312", topics = "history", containerFactory = "batchContainerFactory", clientIdPrefix = "history")
    public void listener(String data, Acknowledgment ack){

        //先将数据存入MySQL
        HistoryInfo historyInfo = storeDB(data);

        //再将数据存入到redis并更改MySQL状态
        storeCache(historyInfo);
        log.info("storeCache success：" + historyInfo);

        ack.acknowledge();
        log.info("consume message success：" + data);

    }

    //这个方法提交到MySQL，存入MySQL时多存入一个状态值，记录数据是否有存入redis
    @Transactional
    public HistoryInfo storeDB(String data){

        HistoryInfo result = JsonUtils.toBean(data, HistoryInfo.class);
        //将数据存储到数据库，如果数据就修改，没有就存入
        HistoryInfoExample example = new HistoryInfoExample();
        example.createCriteria().andUidEqualTo(result.getUid()).andItemTypeEqualTo(result.getItemType()).andGidEqualTo(result.getGid());
        HistoryInfo historyInfo = historyInfoDao.selectOneByExample(example);
        if (historyInfo != null){
            //存在，修改，修改最后时间和次数
            historyInfo.setStatus(false);
            historyInfo.setCount(result.getCount());
            historyInfo.setLastTime(result.getLastTime());
            historyInfoDao.updateByPrimaryKey(historyInfo);
            log.info("storeDB update success：" + historyInfo);
            return historyInfo;
        }else {
            //新增
            result.setStatus(false);
            historyInfoDao.insert(result);
            log.info("storeDB insert success：" + result);
            return result;
        }


    }


    //这个方法将数据存入redis，并更新数据库中的状态
    @Transactional
    public void storeCache(HistoryInfo historyInfo){

        //存入redis，将数据分区，每4000条数据存放到一个kv里

        String key = null;
        int index = 1;
        do {
            key = String.format(KEY_PREFIX, historyInfo.getUid(), historyInfo.getItemType(),index);
            index++;
        }
        while (redisTemplate.opsForHash().size(key) <= 4000);
        String hkey = String.valueOf(historyInfo.getGid());
        String value = String.format(VALUE_PREFIX, historyInfo.getFirstTime().getTime(), historyInfo.getLastTime().getTime(), historyInfo.getCount());


        redisTemplate.opsForHash().put(key, hkey, value);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);

        //修改数据库的状态
        historyInfo.setStatus(true);
        historyInfoDao.updateByPrimaryKey(historyInfo);

    }


}
