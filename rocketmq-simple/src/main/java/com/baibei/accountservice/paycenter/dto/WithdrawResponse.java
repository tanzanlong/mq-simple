package com.baibei.accountservice.paycenter.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 入金响应
 * @author peng
 *
 */
public class WithdrawResponse {
    
    @Setter
    @Getter
    private String orderId;
    
    @Setter
    @Getter
    private String orderStatus;
    
    @Setter
    @Getter
    private Long amount;

}
