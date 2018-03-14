package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Delivery implements Serializable {
 
    private static final long serialVersionUID = -3882829131177602265L;

    //交收单号
    private String orderId;
    
    //用户直属会员编号，如001
    private String orgId;
    
    //用户ID
    private String userId;
    
    //业务系统
    private String businessType;
    
    //交收金额，不包括手续费
    private Long deliveryAmount; 
    
    //平仓金额
    private Long closeAmount;
    
    //平仓收益
    private Long closeGain;
    
    //会员用户ID
    private String memberUserId;
}
