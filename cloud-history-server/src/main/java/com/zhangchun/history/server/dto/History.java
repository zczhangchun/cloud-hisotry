package com.zhangchun.history.server.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchun
 */
@Data
@Builder
public class History {

    private Integer gid;

    private Date firstTime;

    private Date lastTime;

    private Integer count;
}
