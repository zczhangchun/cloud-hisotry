package com.zhangchun.history.check.task;

import com.zhangchun.history.check.dao.HistoryInfoDao;
import com.zhangchun.history.check.model.HistoryInfo;
import com.zhangchun.history.check.model.HistoryInfoExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchun
 */
@EnableScheduling
@Component
@Slf4j
public class CheckTask {

    @Autowired
    private HistoryInfoDao historyInfoDao;

    private static final String KEY_PREFIX = "%s-%s-%s";

    private static final String VALUE_PREFIX = "%s-%s-%s";

    @Autowired
    private StringRedisTemplate redisTemplate;

    //每隔一小时执行
    @Scheduled(cron = "0 */60 * * * ?")
    public void check(){

        //查询数据库中status为false的数据
        HistoryInfoExample example = new HistoryInfoExample();
        example.createCriteria().andStatusEqualTo(false);
        List<HistoryInfo> list = historyInfoDao.selectByExample(example);

        //把这些list数据更新到redis
        String key = null;
        int index = 1;
        for (HistoryInfo historyInfo : list) {
            do {
                key = String.format(KEY_PREFIX, historyInfo.getUid(), historyInfo.getItemType(),index);
                index++;
            }while (redisTemplate.opsForHash().size(key) <= 4000);
            index = 1;

            String hkey = String.valueOf(historyInfo.getGid());
            String value = String.format(VALUE_PREFIX, historyInfo.getFirstTime().getTime(), historyInfo.getLastTime().getTime(), historyInfo.getCount());
            redisTemplate.opsForHash().put(key, hkey, value);
            redisTemplate.expire(key, 1, TimeUnit.DAYS);

        }

        //之后更新数据库信息的状态
        HistoryInfoExample updateExample = new HistoryInfoExample();
        updateExample.createCriteria().andStatusEqualTo(false);
        HistoryInfo condition = HistoryInfo.builder().status(true).build();
        historyInfoDao.updateByExampleSelective(condition, updateExample);



    }

}
