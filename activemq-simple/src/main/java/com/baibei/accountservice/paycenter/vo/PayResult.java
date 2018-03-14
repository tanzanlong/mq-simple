package com.baibei.accountservice.paycenter.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PayResult {

    private String orderId;
    
    private String orderStatus;
    
    private Long amount;

    private String exchange;
}
