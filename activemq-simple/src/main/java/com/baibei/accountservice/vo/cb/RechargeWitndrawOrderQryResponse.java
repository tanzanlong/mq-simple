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
public class RechargeWitndrawOrderQryResponse implements Serializable {

    private static final long serialVersionUID = -7689231591527967502L;

    //用户ID
    private String userId;
    
    //机构ID
    private String orgId;
    
    //时间
    private Date createTime;
    
    //处理时间
    private Date updateTime;
    
    //订单类型, IN=入金，OUT=出金
    private String orderType;
    
    //订单状态 SUCCESS=成功,FAIL=失败，DOING=处理中
    private String orderStatus;
    
    //订单号
    private String orderId;
    
    //渠道编码 JDPAY=京东支付，JDDEFRAY=京东代付
    private String channelCode;
    
    //金额
    private Long amount;
}
