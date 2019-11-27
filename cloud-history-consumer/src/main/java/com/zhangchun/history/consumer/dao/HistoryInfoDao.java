package com.zhangchun.history.consumer.dao;

import com.zhangchun.history.consumer.model.HistoryInfo;
import com.zhangchun.history.consumer.model.HistoryInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryInfoDao {


    int insert(HistoryInfo record);

    int insertSelective(HistoryInfo record);

    HistoryInfo selectOneByExample(HistoryInfoExample example);

    List<HistoryInfo> selectByExample(HistoryInfoExample example);

    int updateByExample(@Param("record") HistoryInfo record, @Param("example") HistoryInfoExample example);

    int updateByPrimaryKeySelective(HistoryInfo record);

    int updateByPrimaryKey(HistoryInfo record);

    HistoryInfo selectByUidAndItemTypeAndGid(@Param("uid") Integer uid, @Param("itemType") Integer itemType, @Param("gid") Integer gid);



}