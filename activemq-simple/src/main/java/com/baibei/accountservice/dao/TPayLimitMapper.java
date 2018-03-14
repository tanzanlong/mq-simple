package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TPayLimit;
import com.baibei.accountservice.model.TPayLimitExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TPayLimitMapper {
    int countByExample(TPayLimitExample example);

    int deleteByExample(TPayLimitExample example);

    int deleteByPrimaryKey(Long accountId);

    int insert(TPayLimit record);

    int insertSelective(TPayLimit record);

    List<TPayLimit> selectByExampleWithRowbounds(TPayLimitExample example, RowBounds rowBounds);

    List<TPayLimit> selectByExample(TPayLimitExample example);

    TPayLimit selectByPrimaryKey(Long accountId);

    int updateByExampleSelective(@Param("record") TPayLimit record, @Param("example") TPayLimitExample example);

    int updateByExample(@Param("record") TPayLimit record, @Param("example") TPayLimitExample example);

    int updateByPrimaryKeySelective(TPayLimit record);

    int updateByPrimaryKey(TPayLimit record);
}