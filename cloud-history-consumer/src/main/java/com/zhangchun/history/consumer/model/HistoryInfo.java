package com.zhangchun.history.consumer.model;

import lombok.Data;

import java.util.Date;

@Data
public class HistoryInfo {
    private Long id;

    private Integer uid;

    private String ip;

    private Integer itemType;

    private Integer gid;

    private Date firstTime;

    private Date lastTime;

    private Integer count;
}