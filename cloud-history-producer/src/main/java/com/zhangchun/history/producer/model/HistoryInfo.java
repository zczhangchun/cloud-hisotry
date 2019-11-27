package com.zhangchun.history.producer.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangchun
 */
@Data
@Builder
public class HistoryInfo {

    private Integer uid;

    private String ip;

    private Integer gid;

    private Long firstTime;

    private Long lastTime;

    private Integer count;

    private Integer itemType;

}
