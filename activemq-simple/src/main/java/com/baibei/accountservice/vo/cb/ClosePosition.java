package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClosePosition implements Serializable {
 
    private static final long serialVersionUID = 1106955153942433026L;
    
    //平仓单号
    private String orderId;
    
    //用户直属会员ID
    private String orgId;
    
    //用户ID
    private String userId;
    
    //会员用户ID
    private String memberUserId;
    
    //业务系统
    private String businessType;
    
    //平仓金额
    private Long amount; 
    
    //盈利金额，亏损则传负数，平则传0
    private Long gain;

}
