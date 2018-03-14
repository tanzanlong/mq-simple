package com.baibei.accountservice.paycenter.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrgFeeDetail implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4483701370943615306L;
    /**
     * 融资手续费
     */
    Long loanPoundage;
    /**
     * 融资手续费
     */
    Long marginPoundage;

    /**
     * 融资利息
     */
    Long loanInterest;

    /**
     * 融货利息
     */
    Long marginInterest;
    
    /**
     * 交易（买货手续费）
     */
    Long buerTradePoundage;
    /**
     * 交易（卖货手续费）
     */
    Long sellerTradePoundage;
}
