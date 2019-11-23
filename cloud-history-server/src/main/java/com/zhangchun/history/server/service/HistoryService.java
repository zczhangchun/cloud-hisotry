package com.zhangchun.history.server.service;

import com.zhangchun.history.server.dao.HistoryDao;
import com.zhangchun.history.server.dao.HistoryInfoDao;
import com.zhangchun.history.server.dto.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchun
 */
@Service
public class HistoryService {

    @Autowired
    private HistoryInfoDao historyInfoDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HistoryDao historyDao;

    private static final String KEY_FORMAT = "%s-%s-%s";

    private static final String VALUE_FORMAT = "%s-%s-%s";

    private static final String LOCK_FORMAT = "lock-%s-%s";

    //获取指定用户指定物品类型的信息
    public List<History> getHistoryByUidAndItemType(Integer uid, Integer itemType) {


        //查询redis缓存
        int index = 1;
        String key = String.format(KEY_FORMAT, uid, itemType, index);
        //有数据，直接封装后返回。
        List<History> historyList = new ArrayList<History>();
        //查看每个uid-itemType分区，每个分区最多有4000条数据
        do {
            //使用渐进式查询
            Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
            boolean flag = cursor.hasNext();
            //如果第一个分区就没有任何数据，查数据库。
            if (!flag && index == 1) {

                //开启分布式锁
                String lockKey = String.format(LOCK_FORMAT, uid, itemType);
                try {
                    Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
                    if (result){
                        //如果拿到锁，查数据库
                        historyList = historyDao.selectByUidAndItemType(uid, itemType);


                        //回写redis
                        writeToCache(key, historyList);
                    }else {
                        //没拿到锁
                        try {
                            while (redisTemplate.hasKey(lockKey)){
                                Thread.sleep(50);
                            }
                            historyList = getHistoryByUidAndItemType(uid, itemType);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                } finally {
                    //最后要将锁删除
                    redisTemplate.delete(lockKey);
                }

            } else {
                //从缓存中获取数据
                do {
                    Map.Entry<Object, Object> next = cursor.next();
                    String value = (String) next.getValue();
                    String[] fileds = value.split("-");

                    historyList.add(History.builder()
                            .firstTime(new Date(Long.valueOf(fileds[0])))
                            .lastTime(new Date(Long.valueOf(fileds[1])))
                            .count(Integer.valueOf(fileds[2]))
                            .gid((Integer) next.getKey())
                            .build());

                } while (cursor.hasNext());
            }
            index++;
            key = String.format(KEY_FORMAT, uid, itemType, index);
        } while (redisTemplate.opsForHash().size(key) > 0);
        return historyList;

    }



    @Async("new_task")
    public void writeToCache(String key, List<History> historyList){

        if (historyList != null) {
            Map<String, String> map = new HashMap<String, String>();
            int index = 1;
            for (History history : historyList) {
                map.put(history.getGid().toString(),
                        String.format(VALUE_FORMAT, history.getFirstTime().getTime(), history.getLastTime().getTime(), history.getCount()));
                if (index % 100 == 0) {
                    redisTemplate.opsForHash().putAll(key, map);
                }
            }
        }else {

            redisTemplate.opsForHash().put(key, "empty","empty");
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);

        }

    }

}
