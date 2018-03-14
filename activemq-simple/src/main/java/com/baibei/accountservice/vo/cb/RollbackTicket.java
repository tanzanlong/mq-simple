package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RollbackTicket implements Serializable {
    
    private static final long serialVersionUID = -7864059398290101710L;

    //建仓单号
    private String orderId;
    
    //用户直属会员编号，如001
    private String orgId;
    
    //用户ID
    private String userId;
    
    //业务系统
    private String businessType;
    
    //券ID列表
    private List<String> ticketIdList;
}
