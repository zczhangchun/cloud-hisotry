package com.zhangchun.history.check.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class HistoryInfo {


    private Long id;

    private Integer uid;

    private String ip;

    private Integer gid;

    private Date firstTime;

    private Date lastTime;

    private Integer count;

    private Boolean status;

    private Integer itemType;
}