package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Freeze implements Serializable {
 
    private static final long serialVersionUID = 1106955153942433026L;
    
    //冻结单号
    private String orderId;
    
    //用户ID
    private String userId;
    
    //业务系统
    private String businessType;
    
    //冻结金额
    private Long amount; 
}
