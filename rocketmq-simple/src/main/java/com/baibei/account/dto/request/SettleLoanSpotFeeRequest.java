package com.baibei.account.dto.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 融货手续费分成请求
 * Created by keegan on 12/05/2017.
 */
@Data
public class SettleLoanSpotFeeRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8317179853359243607L;

    /**
     * 手续费分成
     * Map字段: userId: 用户ID; fee: 手续费(正数为加,负数为减)
     */
    private List<FeeItem> feeList;

    /**
     * 订单号
     */
    private String orderNum;
    
    /**
     * 借入方ID
     */
    private String borrowId;
    
    /**
     * 手续费
     * @author peng
     */
    @Data
    public static class FeeItem implements Serializable{
        
        /**
         * 
         */
        private static final long serialVersionUID = -153863363692691095L;

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
