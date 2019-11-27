package com.zhangchun.history.page.service;

import com.zhangchun.history.page.client.HistoryClient;
import com.zhangchun.history.page.dto.History;
import com.zhangchun.history.page.kafka.producer.GidProducer;
import com.zhangchun.history.page.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchun
 */
@Service
public class PageService {

    @Autowired
    private HistoryClient historyClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GidProducer gidProducer;



    public List<History> getHistoryByUidAndItemType(Integer uid, Integer itemType) {

        List<History> historyList = historyClient.getHistoryByUidAndItemType(uid, itemType);
        //填充historyList，从redis中去查询
        List<Long> list = null;
        String result = null;
        for (History history : historyList) {
             result = redisTemplate.opsForValue().get("gid:" + history.getGid());
            if (result == null){
                if (list == null){
                    list = new ArrayList<Long>();
                }
                list.add(history.getGid());

            }else {
                String[] filed = result.split("-");
                Game game = Game.builder()
                        .id(history.getGid())
                        .img(filed[0])
                        .name(filed[1])
                        .build();

                history.setGame(game);

            }
        }
        if (!CollectionUtils.isEmpty(list)){
            gidProducer.sendMsg(list);
        }
        return historyList;
    }


    public void deleteHistoryByGid(Integer uid, Integer itemType, Integer gid) {

        historyClient.deleteHistoryByGid(uid, itemType, gid);
    }

    public void clearHistoryByItemType(Integer uid, Integer itemType) {

        historyClient.clearHistoryByItemType(uid, itemType);
    }
}
