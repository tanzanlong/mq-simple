package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TAbnormalOrderLog;
import com.baibei.accountservice.model.TAbnormalOrderLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TAbnormalOrderLogMapper {
    int countByExample(TAbnormalOrderLogExample example);

    int deleteByExample(TAbnormalOrderLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAbnormalOrderLog record);

    int insertSelective(TAbnormalOrderLog record);

    List<TAbnormalOrderLog> selectByExampleWithRowbounds(TAbnormalOrderLogExample example, RowBounds rowBounds);

    List<TAbnormalOrderLog> selectByExample(TAbnormalOrderLogExample example);

    TAbnormalOrderLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAbnormalOrderLog record, @Param("example") TAbnormalOrderLogExample example);

    int updateByExample(@Param("record") TAbnormalOrderLog record, @Param("example") TAbnormalOrderLogExample example);

    int updateByPrimaryKeySelective(TAbnormalOrderLog record);

    int updateByPrimaryKey(TAbnormalOrderLog record);
}