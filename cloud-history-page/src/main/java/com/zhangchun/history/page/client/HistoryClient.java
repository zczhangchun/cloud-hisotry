package com.zhangchun.history.page.client;

import com.zhangchun.history.page.dto.History;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author zhangchun
 */
@FeignClient("cloud-history-server")
public interface HistoryClient {

    @RequestMapping(value = "history/{uid}/{itemType}")
    List<History> getHistoryByUidAndItemType(@PathVariable("uid") Integer uid,
                                                                    @PathVariable("itemType") Integer itemType);
    @RequestMapping("history/{uid}/{itemType}/{gid}")
    void deleteHistoryByGid(@PathVariable("uid") Integer uid,
                                                  @PathVariable("itemType") Integer itemType,
                                                  @PathVariable("gid") Integer gid);

    @RequestMapping("history/delete/{uid}/{itemType}")
    void clearHistoryByItemType(@PathVariable("uid") Integer uid,
                                                       @PathVariable("itemType") Integer itemType);

}
