package com.baibei.accountservice.paycenter.dto;

import lombok.Data;

@Data
public class NotifyAbonormalOrderRequest {
    /**
     * 原金额，清结算系统中的金额
     */
    private Long orgAmount;
    /**
     * 渠道金额
     */
    private Long channelAmount;
    /**
     * 原状态，清结算系统中的状态
     */
    private String orgStatus;
    /**
     * 渠道状态
     */
    private String channelStatus;
    /**
     * IN/OUT 入金／出金
     */
    private String orderType;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 消息摘要
     */
    private String sign;
    /**
     * 渠道
     */
    private String channel;

}
