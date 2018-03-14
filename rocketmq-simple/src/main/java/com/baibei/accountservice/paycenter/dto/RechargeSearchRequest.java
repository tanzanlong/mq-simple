package com.baibei.accountservice.paycenter.dto;

import lombok.Getter;
import lombok.Setter;

public class RechargeSearchRequest extends PageBaseRequest{
   
    private static final long serialVersionUID = 3377772558515058710L;

    @Getter
    @Setter
    private String orderId;
    
    @Getter
    @Setter
    private String userId;
}
