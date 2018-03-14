package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

/**
 * 客户收支查询请求
 * @author peng
 */
@Data
@ToString
public class CustomerBalanceLogQryResponse implements Serializable {

    private static final long serialVersionUID = 5924843170793029357L;
    
    //用户ID
    public String userId;
    
    //机构ID
    private String orgId;
    
    //时间
    private Date createTime;
    
    //收支类型 IN=收入，OUT=支出
    private String inOutType;
    
    //订单类型
    private String orderType;
    
    //订单号
    private String orderId;
    
    //金额
    private Long amount;

    //结余
    private Long balance;

    //时间
    private Date updateTime;
}
