package com.zhangchun.history.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zhangchun
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum ItemTypeEnum {

    MAIN(1),
    H5(2),
    GAME(3);

    private int type;
}
