package com.baibei.account.dto.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 扣息请求
 * Created by keegan on 12/05/2017.
 */
@Data
public class SettleLoanInterestRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5876367270217572609L;

    /**
     * 扣息分成
     * Map字段: userId: 用户ID; interest: 利息(正数为加,负数为减)
     */
    private List<FeeItem> interestList;

    /**
     * 订单号
     */
    private String orderNum;
    
    /**
     * 借入方用户ID
     */
    private String borrowId;
    
    /**
     * 类型，fund=融资，spot=融货
     */
    private String type;
    
    @Data
    public static class FeeItem implements Serializable{
        
        /**
         * 
         */
        private static final long serialVersionUID = -792762199922794975L;

        /**
         * 用户ID
         */
        private String userId;
        
        /**
         * 手续费(正数为加,负数为减)
         */
        private Long fee;
        
        /**
         * 直属会员ID
         */
        private String orgId;
    }
}
