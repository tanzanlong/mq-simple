package com.baibei.account.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class FeeAndInterest implements Serializable {
    /**
     * 交易手续费(分)
     */
    private Long tradeFee;

    /**
     * 融资手续费(分)
     */
    private Long loanFundFee;

    /**
     * 融货手续费(分)
     */
    private Long loanSpotFee;

    /**
     * 融资利息(分)
     */
    private Long loanFundInterest;

    /**
     * 融货利息(分)
     */
    private Long loanSpotInterest;

    /**
     * 合计
     */
    private Long total;
}
