package com.zhangchun.history.consumer.dao;

import com.zhangchun.history.consumer.model.HistoryInfoExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * @author zhangchun
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class HistoryInfoDaoTest {

    @Autowired
    private HistoryInfoDao dao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void m1() {
        HistoryInfoExample example = new HistoryInfoExample();
        example.createCriteria().andUidEqualTo(1);

        System.out.println(dao.selectOneByExample(example));

    }

    @Test
    public void m2(){
        redisTemplate.delete("hello");
    }

    @Test
    public void m3(){

        Set<String> keys = redisTemplate.keys("0-*");

        System.out.println(keys);

    }



}
