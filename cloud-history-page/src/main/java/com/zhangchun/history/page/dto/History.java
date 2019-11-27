package com.zhangchun.history.page.dto;

import com.zhangchun.history.page.model.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchun
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {

    private Long gid;

    private Long firstTime;

    private Long lastTime;

    private Integer count;

    private Game game;
}
