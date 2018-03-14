package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TDailyTaskMapper {
    int countByExample(TDailyTaskExample example);

    int deleteByExample(TDailyTaskExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailyTask record);

    int insertSelective(TDailyTask record);

    List<TDailyTask> selectByExampleWithRowbounds(TDailyTaskExample example, RowBounds rowBounds);

    List<TDailyTask> selectByExample(TDailyTaskExample example);

    TDailyTask selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailyTask record, @Param("example") TDailyTaskExample example);

    int updateByExample(@Param("record") TDailyTask record, @Param("example") TDailyTaskExample example);

    int updateByPrimaryKeySelective(TDailyTask record);

    int updateByPrimaryKey(TDailyTask record);
}