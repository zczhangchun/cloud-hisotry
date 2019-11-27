package com.zhangchun.history.server.service;

import com.zhangchun.history.server.dao.HistoryDao;
import com.zhangchun.history.server.dto.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchun
 */
@Service
public class HistoryService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HistoryDao historyDao;

    private static final String KEY_FORMAT = "%s-%s";

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

        //使用渐进式查询
        Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().match("*").count(1000).build());
        boolean flag = cursor.hasNext();
        //如果第一个分区就没有任何数据，查数据库。
        if (!flag) {

            //开启分布式锁
            String lockKey = String.format(LOCK_FORMAT, uid, itemType);
            try {
                Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
                if (result) {
                    //如果拿到锁，查数据库
                    historyList = historyDao.selectByUidAndItemType(uid, itemType);

                    //回写redis
                    writeToCache(key, historyList);
                } else {
                    //没拿到锁
                    try {
                        while (redisTemplate.hasKey(lockKey)) {
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
                Object key1 = next.getKey();

                historyList.add(History.builder()
                        .firstTime(Long.valueOf(fileds[0]))
                        .lastTime(Long.valueOf(fileds[1]))
                        .count(Integer.valueOf(fileds[2]))
                        .gid(Integer.valueOf((String) next.getKey()))
                        .build());

            } while (cursor.hasNext());
        }


        //按时间进行排序
        historyList.sort(new Comparator<History>() {
            public int compare(History o1, History o2) {
                return (int) (o2.getLastTime() - o1.getLastTime());
            }
        });
        return historyList;

    }


    @Async("new_task")
    public void writeToCache(String key, List<History> historyList) {

        if (!CollectionUtils.isEmpty(historyList)) {
            Map<String, String> map = new HashMap<String, String>();
            int index = 1;
            for (History history : historyList) {
                map.put(history.getGid().toString(),
                        String.format(VALUE_FORMAT, history.getFirstTime(), history.getLastTime(), history.getCount()));
                if (index % 100 == 0) {
                    redisTemplate.opsForHash().putAll(key, map);
                    map.clear();
                }
            }

            if (!CollectionUtils.isEmpty(map)) redisTemplate.opsForHash().putAll(key, map);

            //循环结束，将剩下的存入Redis
        } else {

            redisTemplate.opsForHash().put(key, "empty", "empty");
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);

        }

    }

    public void deleteHistoryByGid(Integer uid, Integer itemType, Integer gid) {


        if (uid == null || itemType == null || gid == null) return;

        //删除数据库，在删除缓存
        historyDao.deleteByGid(uid, itemType, gid);

        //删除缓存
        redisTemplate.opsForHash().delete(String.format(KEY_FORMAT, uid, itemType), String.valueOf(gid));


    }

    public void clearHistoryByItemType(Integer uid, Integer itemType) {


        if (uid == null || itemType == null) return;

        historyDao.deleteByItemType(uid, itemType);

        //删除key
        String key = String.format(KEY_FORMAT, uid, itemType);
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        Cursor<Map.Entry<Object, Object>> cursor = operations.scan(ScanOptions.scanOptions().match("*").count(1000).build());
        while (cursor.hasNext()){
            Map.Entry<Object, Object> next = cursor.next();
            operations.delete(next.getKey());
        }



    }
}
