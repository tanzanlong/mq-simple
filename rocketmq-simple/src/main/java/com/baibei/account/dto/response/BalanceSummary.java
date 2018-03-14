package com.baibei.account.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class BalanceSummary implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7731285134236745098L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 期初余额(分)
     */
    private Long beginBalance;

    /**
     * 期末余额(分)
     */
    private Long endBalance;

    /**
     * 入金金额(分)
     */
    private Long inMoney;

    /**
     * 出金金额(分)
     */
    private Long outMoney;

    /**
     * 收入(分)
     */
    private Long income;
}
