package com.zhangchun.history.page.model;

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
public class Game {

    private Long id;

    private String name;

    private String img;

}
