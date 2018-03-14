package com.baibei.accountservice.settlement.business.vo;

import lombok.Data;

@Data
public class Detail {

    private String userId;
    private Long accountId;
    private Long amount;
    private Integer feeType;
}
