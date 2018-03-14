package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TDailySettlement;
import com.baibei.accountservice.model.TDailySettlementExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TDailySettlementMapper {
    int countByExample(TDailySettlementExample example);

    int deleteByExample(TDailySettlementExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailySettlement record);

    int insertSelective(TDailySettlement record);

    List<TDailySettlement> selectByExampleWithRowbounds(TDailySettlementExample example, RowBounds rowBounds);

    List<TDailySettlement> selectByExample(TDailySettlementExample example);

    TDailySettlement selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailySettlement record, @Param("example") TDailySettlementExample example);

    int updateByExample(@Param("record") TDailySettlement record, @Param("example") TDailySettlementExample example);

    int updateByPrimaryKeySelective(TDailySettlement record);

    int updateByPrimaryKey(TDailySettlement record);
}