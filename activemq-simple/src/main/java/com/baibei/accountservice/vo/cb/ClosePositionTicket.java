package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClosePositionTicket implements Serializable {
 
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
    
    //盈利金额，不盈利则传0
    private Long gain;
    
    //券ID列表
    private List<String> ticketIdList;
}
