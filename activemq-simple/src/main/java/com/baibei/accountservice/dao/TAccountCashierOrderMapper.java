package com.baibei.accountservice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TAccountCashierOrder;
import com.baibei.accountservice.model.TAccountCashierOrderExample;

public interface TAccountCashierOrderMapper {
    int countByExample(TAccountCashierOrderExample example);

    int deleteByExample(TAccountCashierOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAccountCashierOrder record);

    int insertSelective(TAccountCashierOrder record);

    List<TAccountCashierOrder> selectByExampleWithRowbounds(TAccountCashierOrderExample example, RowBounds rowBounds);

    List<TAccountCashierOrder> selectByExample(TAccountCashierOrderExample example);

    TAccountCashierOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAccountCashierOrder record, @Param("example") TAccountCashierOrderExample example);

    int updateByExample(@Param("record") TAccountCashierOrder record, @Param("example") TAccountCashierOrderExample example);

    int updateByPrimaryKeySelective(TAccountCashierOrder record);

    int updateByPrimaryKey(TAccountCashierOrder record);
    
    int countCashierOrderSizeByTime(Map<String, Object> params);
    
    List<TAccountCashierOrder> qryAccountCashierOrderList(Map<String, Object> params);
}