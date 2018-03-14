package com.baibei.accountservice.paycenter.dto.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class CorrectAuditTask implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 507865943882552964L;

    /**
     * 方向  IN 入金 OUT 出金
     */
    private String orderType;
    
    /**
     * 订单流水
     */
    private String orderId;
    
    /**
     * 银行流水
     */
    private String channelOrderId;
    
    
    /**
     * 错误类型
     */
    private String errorType;
    
    /**
     * 业务系统侧金额
     */
    private Long orgAmount;
    
    
    /**
     *银行侧金额
     */
    private Long channelAmount;
    
    /**
     * 银行侧状态
     */
    private String channelStatus;
    
    /**
     * 业务系统侧状态
     */
    private String orgStatus;
    
    /**
     * 处理状态 
     */
    private String auditStatus;
    
    
    /**
     * 时间 
     */
    private Date createTime;
    
    /**
     * 银行编码 
     */
    private String bankCode;
}
