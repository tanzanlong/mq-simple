package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 还款请求
 * Created by keegan on 12/05/2017.
 */
@Data
public class SettleRepaymentRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6305307566629541237L;

    /**
     * 借入方ID
     */
    private String borrowId;
    
    /**
     * 借入方直属会员ID
     */
    private String borrowOrgId;

    /**
     * 出借方ID
     */
    private String lenderId;
    
    /**
     * 出借方直属会员ID
     */
    private String lenderOrgId;

    /**
     * 还款（应还）金额(分)
     */
    private Long amount;

    /**
     * 订单号
     */
    private String orderNum;
    
    /**
     * 垫付方垫付金额
     */
    private Long advancedAmount = 0L;
    
}
