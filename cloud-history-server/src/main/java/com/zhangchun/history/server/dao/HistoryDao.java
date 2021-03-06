package com.zhangchun.history.server.dao;

import com.zhangchun.history.server.dto.History;
import com.zhangchun.history.server.model.HistoryInfo;
import com.zhangchun.history.server.model.HistoryInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HistoryDao {

    List<History> selectByUidAndItemType(@Param("uid") int id, @Param("itemType")int itemType);

    void deleteByGid(@Param("uid") Integer uid, @Param("itemType") Integer itemType, @Param("gid") Integer gid);

    void deleteByItemType(@Param("uid") Integer uid,@Param("itemType") Integer itemType);
}