package com.baibei.accountservice.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BalanceLogQryResponse implements Serializable, Comparable<BalanceLogQryResponse> {
    
    private static final long serialVersionUID = -3625984738838509020L;
    
    //时间
    private Date createTime;
    
    //用户ID
    private String userId;

    //订单类型
    private String orderType;
    
    //订单ID
    private String orderId;
    
    //金额
    private Long amount;
    
    //结余
    private Long leftAmount;

    @Override
    public int compareTo(BalanceLogQryResponse blq) {
        if(blq.getCreateTime() != null && this.getCreateTime() != null){
            if (this.getCreateTime().before(blq.getCreateTime())){
                return 1;
            }else{
                return -1;
            }
        }
        return 0;
    }
}
