package com.baibei.accountservice.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;

public interface TAccountCashierLogMapper {
    int countByExample(TAccountCashierLogExample example);

    int deleteByExample(TAccountCashierLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAccountCashierLog record);

    int insertSelective(TAccountCashierLog record);

    List<TAccountCashierLog> selectByExampleWithRowbounds(TAccountCashierLogExample example, RowBounds rowBounds);

    List<TAccountCashierLog> selectByExample(TAccountCashierLogExample example);

    TAccountCashierLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAccountCashierLog record, @Param("example") TAccountCashierLogExample example);

    int updateByExample(@Param("record") TAccountCashierLog record, @Param("example") TAccountCashierLogExample example);

    int updateByPrimaryKeySelective(TAccountCashierLog record);

    int updateByPrimaryKey(TAccountCashierLog record);
    
    List<TAccountCashierLog> getDailySettlementData(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate);
    
    List<TAccountCashierLog> getDailySettlementItemData(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("orderId")String orderId);
    
    Long  queryOrgTotalFee(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("feeItem")String feeItem,@Param("orgId")String orgId);

    Long queryOrgTotalByFeeItem(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,@Param("feeItem")String feeItem,@Param("orgId")String orgId);
   
    List<TAccountCashierLog> getDailySettlementDataPage(Map<String, Object> param);
    
    List<TAccountCashierLog> queryOrderIdPageByUserId(Map<String, Object> param);
}