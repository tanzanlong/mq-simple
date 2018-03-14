package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 贸易请求
 * @author peng
 */
@Data
public class TradeOrderRequest implements Serializable {
   
    /**
     * 
     */
    private static final long serialVersionUID = 5953631141077725776L;

    /**
     * 订单号
     */
    private String orderNum;
    
    /**
     * 订货方用户ID
     */
    private String ordererId;
    
    /**
     * 订货方直属会员ID
     */
    private String ordererOrgId;
    
    /**
     * 金额
     */
    private Long amount;

    /**
     * 发货方用户ID
     */
    private String invoicerId;
    
    /**
     * 发货方用户直属会员ID
     */
    private String invoicerOrgId;
   
   
}
