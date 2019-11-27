package com.zhangchun.history.consumer.dao;

import com.zhangchun.history.consumer.model.Game;

import java.util.List;
import java.util.Set;

/**
 * @author zhangchun
 */

public interface GameDao {

    List<Game> selectByIds(Set ids);

}
