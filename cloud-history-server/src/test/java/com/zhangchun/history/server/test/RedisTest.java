package com.zhangchun.history.server.test;


import com.zhangchun.history.server.dao.HistoryDao;
import com.zhangchun.history.server.dao.HistoryInfoDao;
import com.zhangchun.history.server.dto.History;
import io.lettuce.core.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchun
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HistoryDao historyDao;

    @Autowired
    private Redisson redisson;



    @Test
    public void m1(){

        try {
            Cursor<Map.Entry<Object,Object>> cursor = redisTemplate.opsForHash().scan("1-3", ScanOptions.scanOptions().match("*").count(3).build());
            while (cursor.hasNext()) {

                Map.Entry<Object, Object> next = cursor.next();
                Object key = next.getKey();
                Object valueSet = next.getValue();
                System.out.println(key +  "：" + valueSet);
                System.out.println(cursor.getPosition());
                System.out.println(cursor.getCursorId());
            }
            //关闭cursor
            cursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void m2(){


        Date date = new Date(System.currentTimeMillis());

        String s = String.valueOf(date.getTime());
        System.out.println(Long.valueOf(s));


        RLock lock = redissonClient.getLock("hey_lock");
        lock.lock(10, TimeUnit.SECONDS);

//        String o = (String) redisTemplate.opsForHash().get("1-3", "1");
//        String[] split = o.split("-");
//        System.out.println(split[1]);
//
//        System.out.println(Date.valueOf(split[1]));

    }
    @Autowired
    private HistoryInfoDao historyInfoDao;

    @Test
    public void m4(){

        System.out.println(historyDao.selectByUidAndItemType());

    }
}
