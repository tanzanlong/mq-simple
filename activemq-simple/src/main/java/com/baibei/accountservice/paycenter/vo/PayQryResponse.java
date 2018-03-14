package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 出入金流水查询结果
 * @author peng
 */
@Data
@ToString
public class PayQryResponse implements Serializable{
    
    private static final long serialVersionUID = 443369806976143274L;
    
    //用户ID
    private String userId;
    
    //类型
    private String type;
    
    //金额
    private Long amount;
    
    //订单号
    private String orderId;

    //状态
    private String status;
}
