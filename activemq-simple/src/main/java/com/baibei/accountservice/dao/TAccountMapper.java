package com.baibei.accountservice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountExample;

public interface TAccountMapper {
    int countByExample(TAccountExample example);

    int deleteByExample(TAccountExample example);

    int deleteByPrimaryKey(Long accountId);

    int insert(TAccount record);

    int insertSelective(TAccount record);

    List<TAccount> selectByExampleWithRowbounds(TAccountExample example, RowBounds rowBounds);

    List<TAccount> selectByExample(TAccountExample example);

    TAccount selectByPrimaryKey(Long accountId);

    int updateByExampleSelective(@Param("record") TAccount record, @Param("example") TAccountExample example);

    int updateByExample(@Param("record") TAccount record, @Param("example") TAccountExample example);

    int updateByPrimaryKeySelective(TAccount record);

    int updateByPrimaryKey(TAccount record);
    
    int insertBatch(List<TAccount> records);
    
    int countAccountSize();
    
    List<TAccount> selectAccountIdByPage(Map<String, Object> map);

}