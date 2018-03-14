package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TDailySettlementRes;
import com.baibei.accountservice.model.TDailySettlementResExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TDailySettlementResMapper {
    int countByExample(TDailySettlementResExample example);

    int deleteByExample(TDailySettlementResExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailySettlementRes record);

    int insertSelective(TDailySettlementRes record);

    List<TDailySettlementRes> selectByExampleWithRowbounds(TDailySettlementResExample example, RowBounds rowBounds);

    List<TDailySettlementRes> selectByExample(TDailySettlementResExample example);

    TDailySettlementRes selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailySettlementRes record, @Param("example") TDailySettlementResExample example);

    int updateByExample(@Param("record") TDailySettlementRes record, @Param("example") TDailySettlementResExample example);

    int updateByPrimaryKeySelective(TDailySettlementRes record);

    int updateByPrimaryKey(TDailySettlementRes record);
}