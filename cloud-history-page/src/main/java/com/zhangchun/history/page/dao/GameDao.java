package com.zhangchun.history.page.dao;

import com.zhangchun.history.page.model.Game;

import java.util.List;

/**
 * @author zhangchun
 */
public interface GameDao {

    List<Game> selectByIds(List<Long> ids);

}
