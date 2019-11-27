package com.zhangchun.history.server.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhangchun
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private Integer gid;

    private Long firstTime;

    private Long lastTime;

    private Integer count;
}
