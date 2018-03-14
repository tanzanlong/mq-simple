package com.baibei.accountservice.account.vo;

import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 账户余额变更求
 * @author peng
 *
 */
@Data
@ToString
public class AccountBalanceModifyReq {

    //业务系统
    private String businessType;
    
    //订单类型
    private String orderType;
    
    //订单号
    private String orderId;
    
    //明细
    private List<Detail> detailList;
    
    @Data
    public static class Detail{
        
        //用户ID
        private String userId;
        
        //账户ID
        private Long accountId;
        
        //变更金额
        private Long amount;
        
        //余额类型
        private String balanceType;
        
        //费项
        private String feeItem;
        
        //直属机构ID
        private String orgId;
    }
}
