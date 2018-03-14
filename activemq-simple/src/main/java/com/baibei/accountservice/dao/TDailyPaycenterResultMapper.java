package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TDailyPaycenterResult;
import com.baibei.accountservice.model.TDailyPaycenterResultExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TDailyPaycenterResultMapper {
    int countByExample(TDailyPaycenterResultExample example);

    int deleteByExample(TDailyPaycenterResultExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailyPaycenterResult record);

    int insertSelective(TDailyPaycenterResult record);

    List<TDailyPaycenterResult> selectByExampleWithRowbounds(TDailyPaycenterResultExample example, RowBounds rowBounds);

    List<TDailyPaycenterResult> selectByExample(TDailyPaycenterResultExample example);

    TDailyPaycenterResult selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailyPaycenterResult record, @Param("example") TDailyPaycenterResultExample example);

    int updateByExample(@Param("record") TDailyPaycenterResult record, @Param("example") TDailyPaycenterResultExample example);

    int updateByPrimaryKeySelective(TDailyPaycenterResult record);

    int updateByPrimaryKey(TDailyPaycenterResult record);
}