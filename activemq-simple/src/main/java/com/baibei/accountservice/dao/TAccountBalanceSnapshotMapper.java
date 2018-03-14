package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TAccountBalanceSnapshot;
import com.baibei.accountservice.model.TAccountBalanceSnapshotExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TAccountBalanceSnapshotMapper {
    int countByExample(TAccountBalanceSnapshotExample example);

    int deleteByExample(TAccountBalanceSnapshotExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TAccountBalanceSnapshot record);

    int insertSelective(TAccountBalanceSnapshot record);

    List<TAccountBalanceSnapshot> selectByExampleWithRowbounds(TAccountBalanceSnapshotExample example, RowBounds rowBounds);

    List<TAccountBalanceSnapshot> selectByExample(TAccountBalanceSnapshotExample example);

    TAccountBalanceSnapshot selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TAccountBalanceSnapshot record, @Param("example") TAccountBalanceSnapshotExample example);

    int updateByExample(@Param("record") TAccountBalanceSnapshot record, @Param("example") TAccountBalanceSnapshotExample example);

    int updateByPrimaryKeySelective(TAccountBalanceSnapshot record);

    int updateByPrimaryKey(TAccountBalanceSnapshot record);
}