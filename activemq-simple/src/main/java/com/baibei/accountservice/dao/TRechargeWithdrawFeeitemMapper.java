package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TRechargeWithdrawFeeitem;
import com.baibei.accountservice.model.TRechargeWithdrawFeeitemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TRechargeWithdrawFeeitemMapper {
    int countByExample(TRechargeWithdrawFeeitemExample example);

    int deleteByExample(TRechargeWithdrawFeeitemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TRechargeWithdrawFeeitem record);

    int insertSelective(TRechargeWithdrawFeeitem record);

    List<TRechargeWithdrawFeeitem> selectByExampleWithRowbounds(TRechargeWithdrawFeeitemExample example, RowBounds rowBounds);

    List<TRechargeWithdrawFeeitem> selectByExample(TRechargeWithdrawFeeitemExample example);

    TRechargeWithdrawFeeitem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TRechargeWithdrawFeeitem record, @Param("example") TRechargeWithdrawFeeitemExample example);

    int updateByExample(@Param("record") TRechargeWithdrawFeeitem record, @Param("example") TRechargeWithdrawFeeitemExample example);

    int updateByPrimaryKeySelective(TRechargeWithdrawFeeitem record);

    int updateByPrimaryKey(TRechargeWithdrawFeeitem record);
}