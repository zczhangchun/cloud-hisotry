package com.zhangchun.history.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchun
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private Long id;

    private String userId;

    private Integer userType;

    private Integer itemType;

    private Integer itemId;

    private Long firstTime;

    private Long lastTime;

    private Long count;

}
