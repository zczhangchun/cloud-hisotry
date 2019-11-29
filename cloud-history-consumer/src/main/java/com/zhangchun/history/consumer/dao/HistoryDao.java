package com.zhangchun.history.consumer.dao;

import com.zhangchun.history.consumer.model.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zhangchun
 */
@Mapper
public interface HistoryDao {

    void insertList(List<History> historyList);

}
