package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

/**
 * 客户收支查询请求
 * @author peng
 */
@Data
@ToString
public class CustomerBalanceLogQryRequest implements Serializable {

    private static final long serialVersionUID = 5924843170793029357L;
    
    //用户ID
    public String userId;

    //userIdList
    private List<String> userIdList;
    
    //机构ID
    private String orgId;
    
    //开始时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    
    //结束时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    
    //收支类型 IN=收入，OUT=支出
    private String inOutType;
    
    //订单类型
    private String orderType;
    
    //订单号
    private String orderId;
    
    //页码
    private int pageNo;
    
    //每页记录条数
    private int pageSize = 10;

}
