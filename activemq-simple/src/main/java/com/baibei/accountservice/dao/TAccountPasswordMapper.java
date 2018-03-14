package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TAccountPassword;
import com.baibei.accountservice.model.TAccountPasswordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TAccountPasswordMapper {
    int countByExample(TAccountPasswordExample example);

    int deleteByExample(TAccountPasswordExample example);

    int deleteByPrimaryKey(Long accountId);

    int insert(TAccountPassword record);

    int insertSelective(TAccountPassword record);

    List<TAccountPassword> selectByExampleWithRowbounds(TAccountPasswordExample example, RowBounds rowBounds);

    List<TAccountPassword> selectByExample(TAccountPasswordExample example);

    TAccountPassword selectByPrimaryKey(Long accountId);

    int updateByExampleSelective(@Param("record") TAccountPassword record, @Param("example") TAccountPasswordExample example);

    int updateByExample(@Param("record") TAccountPassword record, @Param("example") TAccountPasswordExample example);

    int updateByPrimaryKeySelective(TAccountPassword record);

    int updateByPrimaryKey(TAccountPassword record);
}