package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OpenPosition implements Serializable {
 
    private static final long serialVersionUID = 1106955153942433025L;
    
    //建仓单号
    private String orderId;
    
    //用户直属会员编号，如001
    private String orgId;
    
    //用户ID
    private String userId;
    
    //业务系统
    private String businessType;
    
    //建仓金额，不包括手续费
    private Long amount; 
    
    //手续费分成列表
    private List<FeeItem> feeItemList;
    
    @Data
    @ToString
    public static class FeeItem {
        //用户ID
        private String userId;
        
        //用户直属会员ID
        private String orgId;
        
        //手续费费用，增加为正数，扣除为负数
        private Long fee;
    }

}
