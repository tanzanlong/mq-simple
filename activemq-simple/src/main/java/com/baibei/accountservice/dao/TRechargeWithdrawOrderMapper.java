package com.baibei.accountservice.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;

public interface TRechargeWithdrawOrderMapper {
    int countByExample(TRechargeWithdrawOrderExample example);

    int deleteByExample(TRechargeWithdrawOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TRechargeWithdrawOrder record);

    int insertSelective(TRechargeWithdrawOrder record);

    List<TRechargeWithdrawOrder> selectByExampleWithRowbounds(TRechargeWithdrawOrderExample example, RowBounds rowBounds);

    List<TRechargeWithdrawOrder> selectByExample(TRechargeWithdrawOrderExample example);

    TRechargeWithdrawOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TRechargeWithdrawOrder record, @Param("example") TRechargeWithdrawOrderExample example);

    int updateByExample(@Param("record") TRechargeWithdrawOrder record, @Param("example") TRechargeWithdrawOrderExample example);

    int updateByPrimaryKeySelective(TRechargeWithdrawOrder record);

    int updateByPrimaryKey(TRechargeWithdrawOrder record);    
    
//    List<TRechargeWithdrawOrder> getDailyTWithdrawOrderData(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("orderType")String orderType);

    Long sumAmount(Map<String, Object> params);
}