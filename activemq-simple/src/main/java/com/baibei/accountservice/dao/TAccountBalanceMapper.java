package com.baibei.accountservice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;

public interface TAccountBalanceMapper {
    int countByExample(TAccountBalanceExample example);

    int deleteByExample(TAccountBalanceExample example);

    int deleteByPrimaryKey(Long accountBalanceId);

    int insert(TAccountBalance record);

    int insertSelective(TAccountBalance record);

    List<TAccountBalance> selectByExampleWithRowbounds(TAccountBalanceExample example, RowBounds rowBounds);

    List<TAccountBalance> selectByExample(TAccountBalanceExample example);

    TAccountBalance selectByPrimaryKey(Long accountBalanceId);

    int updateByExampleSelective(@Param("record") TAccountBalance record, @Param("example") TAccountBalanceExample example);

    int updateByExample(@Param("record") TAccountBalance record, @Param("example") TAccountBalanceExample example);

    int updateByPrimaryKeySelective(TAccountBalance record);

    int updateByPrimaryKey(TAccountBalance record);
    
    int insertBatch(List<TAccountBalance> records);
    
    Long sumAmount(Map<String, Object> params);
}