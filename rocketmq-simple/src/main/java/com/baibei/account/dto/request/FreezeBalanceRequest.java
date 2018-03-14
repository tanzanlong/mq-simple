package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class FreezeBalanceRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5420209591156654483L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 冻结金额(分)
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
