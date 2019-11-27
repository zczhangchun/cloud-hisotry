package com.zhangchun.history.page.controller;

import com.zhangchun.history.page.dto.History;
import com.zhangchun.history.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangchun
 */
@RestController
public class PageController {

    @Autowired
    private PageService pageService;

    @RequestMapping("/{uid}/{itemType}")
    public ResponseEntity<List<History>> getHistoryByUidAndItemType(@PathVariable("uid") Integer uid,
                                                                    @PathVariable("itemType") Integer itemType){

        return ResponseEntity.ok(pageService.getHistoryByUidAndItemType(uid, itemType));
    }

    @RequestMapping("/{uid}/{itemType}/{gid}")
    public ResponseEntity<Void> deleteHistoryByGid(@PathVariable("uid") Integer uid,
                                                  @PathVariable("itemType") Integer itemType,
                                                  @PathVariable("gid") Integer gid){

        pageService.deleteHistoryByGid(uid, itemType, gid);
        return ResponseEntity.ok().build();

    }

    @RequestMapping("delete/{uid}/{itemType}")
    public ResponseEntity<Void> clearHistoryByItemType(@PathVariable("uid") Integer uid,
                                                       @PathVariable("itemType") Integer itemType){

        pageService.clearHistoryByItemType(uid, itemType);
        return ResponseEntity.ok().build();
    }

}
