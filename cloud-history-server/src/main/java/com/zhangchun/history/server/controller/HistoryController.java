package com.zhangchun.history.server.controller;

import com.zhangchun.history.server.dto.History;
import com.zhangchun.history.server.service.HistoryService;
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
@RequestMapping("history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;


    @RequestMapping(value = "{uid}/{itemType}")
    public ResponseEntity<List<History>> getHistoryByUidAndItemType(@PathVariable("uid") Integer uid,
                                                                    @PathVariable("itemType") Integer itemType){

        return ResponseEntity.ok(historyService.getHistoryByUidAndItemType(uid, itemType));

    }
}
