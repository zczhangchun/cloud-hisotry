package com.zhangchun.history.consumer.dao;

import com.zhangchun.history.consumer.model.History;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchun
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HistoryDaoTest {

    @Autowired
    private HistoryDao historyDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript defaultRedisScript;

    @Test
    public void m1(){

        List<History> historyList = new ArrayList<>();

        historyList.add(History.builder()
                .userType(1)
                .userId("1102f8YrHLd0at4UsC5rrf31d")
                .itemId(2)
                .itemType(1)
                .firstTime(1574999640698l)
                .lastTime(1574999640999l)
                .count(2l).build());
        historyList.add(History.builder()
                .userType(2)
                .userId("2915698125")
                .itemId(1)
                .itemType(2)
                .firstTime(1574999641698l)
                .lastTime(1574999641999l)
                .count(3l).build());

        historyDao.insertList(historyList);

    }

    @Test
    public void m2(){
        List<String> list = new ArrayList<>();
        list.add("script");
        list.add("1");
//        list.add("hey");

        System.out.println(defaultRedisScript.getScriptAsString());
        redisTemplate.execute(defaultRedisScript, list, "asd","12");
        System.out.println(redisTemplate.opsForHash().get("script","1"));


    }




}