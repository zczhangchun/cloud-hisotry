package com.zhangchun.history.server.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author zhangchun
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class HistoryDaoTest {

    @Autowired
    private HistoryDao historyDao;

    @Test
    public void selectByUidAndItemType() {

        System.out.println(historyDao.selectByUidAndItemType(1, 1));

    }

}