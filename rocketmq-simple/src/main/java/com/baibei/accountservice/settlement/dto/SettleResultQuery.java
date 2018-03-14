package com.baibei.accountservice.settlement.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SettleResultQuery implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -7242494093463332858L;

    /**
     * 结算日期
     */
    private String settleDate;
    
    /**
     * 银行
     */
    private String bank;
}
