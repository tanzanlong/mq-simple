package com.baibei.account.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by keegan on 11/05/2017.
 */
@Data
public class BalanceAndSignedStatus implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4034976453151916521L;

    /**
     * 余额
     */
    private Balance balance;

    /**
     * 是否已签约
     */
    private Boolean isSigned;

    /**
     * 签约银行编号
     */
    private String signBankNo;
    /**
     * 签约银行
     */
    private String signChannel;
    /**
     * 银行账号
     */
    private String signAccountId;
    /**
     * 资金帐号id
     */
    private Long accountId;
    

}
