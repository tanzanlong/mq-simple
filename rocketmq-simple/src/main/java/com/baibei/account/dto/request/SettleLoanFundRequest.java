package com.baibei.account.dto.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 融资请求
 * Created by keegan on 12/05/2017.
 */
@Data
public class SettleLoanFundRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8615887593498224217L;

    /**
     * 借入方ID
     */
    private String borrowId;
    
    /**
     * 借入方直属会员ID
     */
    private String borrowOrgId;

    /**
     * 出借方ID
     */
    private String lenderId;
    
    /**
     * 出借方直属会员ID
     */
    private String lenderOrgId;

    /**
     * 借款金额(分)
     */
    private Long amount;

    /**
     * 手续费分成
     */
    private List<FeeItem> feeList;

    /**
     * 订单号
     */
    private String orderNum;

    /**
     * 手续费
     * @author peng
     */
    @Data
    public static class FeeItem implements Serializable {
        
        /**
         * 
         */
        private static final long serialVersionUID = 8799323465566451153L;

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
