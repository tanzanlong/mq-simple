package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TFourElement;
import com.baibei.accountservice.model.TFourElementExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TFourElementMapper {
    int countByExample(TFourElementExample example);

    int deleteByExample(TFourElementExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TFourElement record);

    int insertSelective(TFourElement record);

    List<TFourElement> selectByExampleWithRowbounds(TFourElementExample example, RowBounds rowBounds);

    List<TFourElement> selectByExample(TFourElementExample example);

    TFourElement selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TFourElement record, @Param("example") TFourElementExample example);

    int updateByExample(@Param("record") TFourElement record, @Param("example") TFourElementExample example);

    int updateByPrimaryKeySelective(TFourElement record);

    int updateByPrimaryKey(TFourElement record);
}