package com.baibei.account.dto.response;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class OrgBalanceSummary implements Serializable {
    /**
     * orgID
     */
    private String orgId;

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
