package com.baibei.accountservice.settlement.business.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SettlementItem {

    public String businessType;
    
    private String orderId;
    
    private String orderType;
    
    private Date orderTime;
    
    private String productCode = "";
    
    private List<Detail> items = new ArrayList<Detail>();
}
