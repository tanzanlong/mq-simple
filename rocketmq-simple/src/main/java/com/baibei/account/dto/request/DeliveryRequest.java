package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 交收请求
 * @author peng
 */
@Data
public class DeliveryRequest implements Serializable {
   
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
     * 订货方收入金额，负数表示支出
     */
    private Long ordererIncomeAmount;

    /**
     * 发货方用户ID
     */
    private String invoicerId;
    
    /**
     * 发货方用户直属会员ID
     */
    private String invoicerOrgId;
    
    /**
     * 发货方收入金额，负数表示支出
     */
    private Long invoicerIncomeAmount;
   
}
