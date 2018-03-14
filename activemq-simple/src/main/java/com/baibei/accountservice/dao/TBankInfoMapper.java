package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TBankInfo;
import com.baibei.accountservice.model.TBankInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TBankInfoMapper {
    int countByExample(TBankInfoExample example);

    int deleteByExample(TBankInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TBankInfo record);

    int insertSelective(TBankInfo record);

    List<TBankInfo> selectByExampleWithRowbounds(TBankInfoExample example, RowBounds rowBounds);

    List<TBankInfo> selectByExample(TBankInfoExample example);

    TBankInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TBankInfo record, @Param("example") TBankInfoExample example);

    int updateByExample(@Param("record") TBankInfo record, @Param("example") TBankInfoExample example);

    int updateByPrimaryKeySelective(TBankInfo record);

    int updateByPrimaryKey(TBankInfo record);
}