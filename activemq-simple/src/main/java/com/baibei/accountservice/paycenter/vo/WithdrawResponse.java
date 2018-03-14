package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 出金响应
 * @author peng
 */
@Data
@ToString
public class WithdrawResponse implements Serializable{
    
    private static final long serialVersionUID = -6069511207158005321L;

    private String orderId;
    
    private String orderStatus;
    
    private Long amount;
}
