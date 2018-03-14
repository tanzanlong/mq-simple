package com.baibei.accountservice.paycenter.dto.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class WithdrawResponse implements Serializable{
   
    private static final long serialVersionUID = 2079294744948694035L;

    @Getter
    @Setter
    private String orderId;
   
    @Getter
    @Setter
    private Long amount;
   
    @Getter
    @Setter
    private Date createTime;
   
    @Getter
    @Setter
    private String orderStatus;

}
