package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TDailySettlementItem;
import com.baibei.accountservice.model.TDailySettlementItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TDailySettlementItemMapper {
    int countByExample(TDailySettlementItemExample example);

    int deleteByExample(TDailySettlementItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TDailySettlementItem record);

    int insertSelective(TDailySettlementItem record);

    List<TDailySettlementItem> selectByExampleWithRowbounds(TDailySettlementItemExample example, RowBounds rowBounds);

    List<TDailySettlementItem> selectByExample(TDailySettlementItemExample example);

    TDailySettlementItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TDailySettlementItem record, @Param("example") TDailySettlementItemExample example);

    int updateByExample(@Param("record") TDailySettlementItem record, @Param("example") TDailySettlementItemExample example);

    int updateByPrimaryKeySelective(TDailySettlementItem record);

    int updateByPrimaryKey(TDailySettlementItem record);
}