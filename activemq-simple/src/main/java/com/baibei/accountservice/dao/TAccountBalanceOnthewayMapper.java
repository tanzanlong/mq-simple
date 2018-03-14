package com.baibei.accountservice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.comm.SqlVo;
import com.baibei.accountservice.model.TAccountBalanceOntheway;
import com.baibei.accountservice.model.TAccountBalanceOnthewayExample;

public interface TAccountBalanceOnthewayMapper {
    int countByExample(TAccountBalanceOnthewayExample example);

    int deleteByExample(TAccountBalanceOnthewayExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAccountBalanceOntheway record);

    int insertSelective(TAccountBalanceOntheway record);

    List<TAccountBalanceOntheway> selectByExampleWithRowbounds(TAccountBalanceOnthewayExample example, RowBounds rowBounds);

    List<TAccountBalanceOntheway> selectByExample(TAccountBalanceOnthewayExample example);

    TAccountBalanceOntheway selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAccountBalanceOntheway record, @Param("example") TAccountBalanceOnthewayExample example);

    int updateByExample(@Param("record") TAccountBalanceOntheway record, @Param("example") TAccountBalanceOnthewayExample example);

    int updateByPrimaryKeySelective(TAccountBalanceOntheway record);

    int updateByPrimaryKey(TAccountBalanceOntheway record);
    
    int insertBatch(List<TAccountBalanceOntheway> records);
    
    int insertBySql(SqlVo sqlVo);
}