package com.baibei.accountservice.paycenter.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TicketBondsRecharge {

    private String businessType;
    
    private String userId;
    
    private Long amount;
}
