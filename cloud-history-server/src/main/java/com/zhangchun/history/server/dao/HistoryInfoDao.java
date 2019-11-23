package com.zhangchun.history.server.dao;

import com.zhangchun.history.server.model.HistoryInfo;
import com.zhangchun.history.server.model.HistoryInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryInfoDao {


    long countByExample(HistoryInfoExample example);

    int deleteByExample(HistoryInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(HistoryInfo record);

    int insertSelective(HistoryInfo record);

    List<HistoryInfo> selectByExample(HistoryInfoExample example);

    HistoryInfo selectOneByExample(HistoryInfoExample example);

    HistoryInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") HistoryInfo record, @Param("example") HistoryInfoExample example);

    int updateByExample(@Param("record") HistoryInfo record, @Param("example") HistoryInfoExample example);

    int updateByPrimaryKeySelective(HistoryInfo record);

    int updateByPrimaryKey(HistoryInfo record);
}