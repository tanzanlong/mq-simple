package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TAppConfig;
import com.baibei.accountservice.model.TAppConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TAppConfigMapper {
    int countByExample(TAppConfigExample example);

    int deleteByExample(TAppConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAppConfig record);

    int insertSelective(TAppConfig record);

    List<TAppConfig> selectByExampleWithRowbounds(TAppConfigExample example, RowBounds rowBounds);

    List<TAppConfig> selectByExample(TAppConfigExample example);

    TAppConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAppConfig record, @Param("example") TAppConfigExample example);

    int updateByExample(@Param("record") TAppConfig record, @Param("example") TAppConfigExample example);

    int updateByPrimaryKeySelective(TAppConfig record);

    int updateByPrimaryKey(TAppConfig record);
}