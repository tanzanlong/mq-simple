package com.baibei.accountservice.dao;

import com.baibei.accountservice.model.TCustomer;
import com.baibei.accountservice.model.TCustomerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TCustomerMapper {
    int countByExample(TCustomerExample example);

    int deleteByExample(TCustomerExample example);

    int deleteByPrimaryKey(Long customerId);

    int insert(TCustomer record);

    int insertSelective(TCustomer record);

    List<TCustomer> selectByExampleWithRowbounds(TCustomerExample example, RowBounds rowBounds);

    List<TCustomer> selectByExample(TCustomerExample example);

    TCustomer selectByPrimaryKey(Long customerId);

    int updateByExampleSelective(@Param("record") TCustomer record, @Param("example") TCustomerExample example);

    int updateByExample(@Param("record") TCustomer record, @Param("example") TCustomerExample example);

    int updateByPrimaryKeySelective(TCustomer record);

    int updateByPrimaryKey(TCustomer record);
}