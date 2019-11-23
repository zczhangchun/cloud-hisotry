package com.zhangchun.history.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Boolean status;

    private Integer itemType;
}