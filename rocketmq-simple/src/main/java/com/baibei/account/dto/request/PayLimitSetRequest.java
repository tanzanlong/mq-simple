package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class PayLimitSetRequest implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -7344012952655271522L;
    /**
     * 用户id
     */
    private String userId;
    /**
      * 出入金标识 IN 入金  OUT 出金
     */
    private String withdrawOrRecharge;
    /**
     * 是否限制
     */
    private Boolean isCanPay;
}
