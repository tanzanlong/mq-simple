package com.baibei.account.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 余额
 * Created by keegan on 11/05/2017.
 */
@Data
public class Balance implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6358378182558355195L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 总余额(分)
     */
    private Long totalBalance;

    /**
     * 可用金额(分)
     */
    private Long availableBalance;

    /**
     * 冻结金额(分)
     */
    private Long frozenBalance;

    /**
     * 融资未还金额(分)
     */
    private Long loanBalance;

    /**
     * 其他未付款(分)
     */
    private Long otherUnpaid;
    
    /**
     * 可提(分)
     */
    private Long canWithdraw;

}
