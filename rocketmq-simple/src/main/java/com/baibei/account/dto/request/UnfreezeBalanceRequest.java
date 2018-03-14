package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class UnfreezeBalanceRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8403890921873517438L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 解冻金额(分)
     */
    private Long amount;

    /**
     * 委托单号
     */
    private String entrustNum;
    
    /**
     * 直属会员ID
     */
    private String orgId;
}
