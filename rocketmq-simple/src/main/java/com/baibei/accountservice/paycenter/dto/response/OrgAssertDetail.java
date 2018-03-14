package com.baibei.accountservice.paycenter.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrgAssertDetail implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -4022353281400156497L;
    /**
     * 期初余额
     */
    Long beginBalance;
    /**
     * 期末余额
     */
    Long endBalance;
    /**
     * 入金额
     */
    Long rechargeAmount;
    /**
     * 出金额
     */
    Long withdrawAmount;
    
    
    /**
     * 当期收入
     */
    Long inCome;
}
