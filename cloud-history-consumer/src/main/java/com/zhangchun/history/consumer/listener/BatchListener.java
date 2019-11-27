package com.zhangchun.history.consumer.listener;

import com.zhangchun.history.consumer.dao.GameDao;
import com.zhangchun.history.consumer.dao.HistoryInfoDao;
import com.zhangchun.history.consumer.model.Game;
import com.zhangchun.history.consumer.model.HistoryInfo;
import com.zhangchun.history.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private GameDao gameDao;

    private static final String KEY_PREFIX = "%s-%s";

    private static final String VALUE_PREFIX = "%s-%s-%s";

    private static final String GAME_FORMAT = "%s-%s";


    // 手动提交ack
    @KafkaListener(id = "12312", topics = "history", containerFactory = "batchContainerFactory", clientIdPrefix = "history")
    public void listener(String data, Acknowledgment ack) {

        //先将数据存入MySQL
        HistoryInfo historyInfo = storeDB(data);

        //再将数据存入到redis并更改MySQL状态
        storeCache(historyInfo);
        log.info("storeCache success：" + historyInfo);

        ack.acknowledge();
        log.info("consume message success：" + data);

    }

    @KafkaListener(id = "new-consumer-test", topics = "expire", containerFactory = "batchContainerFactory", clientIdPrefix = "expire")
    public void expireListener(Set<Integer> ids, Acknowledgment ack){


        //更新redis
        System.out.println(ids);
        List<Game> games = gameDao.selectByIds(ids);
        Map<String,String> map = new HashMap<String, String>();
        Map<String,String> expireMap = new HashMap<String, String>();
        for (Game game : games) {
            map.put("gid:"+game.getId(), String.format(GAME_FORMAT, game.getImg(), game.getName()));
            map.put(game.getId() + "_sign","");
        }



        redisTemplate.opsForValue().multiSet(map);
        redisTemplate.opsForValue().multiSet(expireMap);

        //提交
        ack.acknowledge();

    }

    //这个方法提交到MySQL，存入MySQL时多存入一个状态值，记录数据是否有存入redis
    @Transactional
    public HistoryInfo storeDB(String data) {

        HistoryInfo result = JsonUtils.toBean(data, HistoryInfo.class);
        //将数据存储到数据库，如果数据存在就修改，没有就存入
        HistoryInfo historyInfo = historyInfoDao.selectByUidAndItemTypeAndGid(result.getUid(), result.getItemType(), result.getGid());
        if (historyInfo != null) {
            //存在，修改，修改最后时间和次数
            historyInfo.setCount(result.getCount());
            historyInfo.setLastTime(result.getLastTime());
            historyInfoDao.updateByPrimaryKey(historyInfo);
            log.info("storeDB update success：" + historyInfo);
            return historyInfo;
        } else {
            //新增
            historyInfoDao.insert(result);
            log.info("storeDB insert success：" + result);
            return result;
        }


    }


    //这个方法将数据存入redis，并更新数据库中的状态
    @Transactional
    public void storeCache(HistoryInfo historyInfo) {

        //存入redis
        String key = null;
        key = String.format(KEY_PREFIX, historyInfo.getUid(), historyInfo.getItemType());
        String hkey = String.valueOf(historyInfo.getGid());
        String value = String.format(VALUE_PREFIX, historyInfo.getFirstTime().getTime(), historyInfo.getLastTime().getTime(), historyInfo.getCount());


        //存入redis数据
        redisTemplate.opsForHash().put(key, hkey, value);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);


    }


}
