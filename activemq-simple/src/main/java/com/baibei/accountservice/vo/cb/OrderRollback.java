package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 订单余额变更回退
 * @author peng
 */
@Data
@ToString
public class OrderRollback implements Serializable {
 
    private static final long serialVersionUID = -3917248816838088630L;

    //订单类型,建仓：OPENPOSITION，交收：DELIVERY
    private String orderType;
    
    //订单号
    private String orderId;
    
    //业务系统
    private String businessType;

}
