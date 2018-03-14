package com.baibei.accountservice.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.baibei.accountservice.model.TTicket;
import com.baibei.accountservice.model.TTicketExample;

public interface TTicketMapper {
    int countByExample(TTicketExample example);

    int deleteByExample(TTicketExample example);

    int deleteByPrimaryKey(String id);

    int insert(TTicket record);

    int insertSelective(TTicket record);

    List<TTicket> selectByExampleWithRowbounds(TTicketExample example, RowBounds rowBounds);

    List<TTicket> selectByExample(TTicketExample example);

    TTicket selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TTicket record, @Param("example") TTicketExample example);

    int updateByExample(@Param("record") TTicket record, @Param("example") TTicketExample example);

    int updateByPrimaryKeySelective(TTicket record);

    int updateByPrimaryKey(TTicket record);
    
    int batchInsert(List<TTicket> records);
    
    int batchGive(Map<String, Object> params);
    
    Long sumUnusedTicketValue(Map<String, Object> params);
    
    int updateStatusBatch(Map<String, Object> params);
}