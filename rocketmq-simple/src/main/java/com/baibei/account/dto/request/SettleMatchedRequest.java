package com.baibei.account.dto.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 撮合成交资金结算请求
 * Created by keegan on 12/05/2017.
 */
@Data
public class SettleMatchedRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3838852750494761554L;

    /**
     * 买方ID
     */
    private String buyerId;
    
    /**
     * 买方用户直属会员ID
     */
    private String bueryOrgId;

    /**
     * 卖方ID
     */
    private String sellerId;
    
    /**
     * 卖方用户直属会员ID
     */
    private String sellerOrgId;

    /**
     * 交易总金额(分)
     */
    private Long amount;

    /**
     * 手续费分成
     */
    private List<FeeItem> feeList;

    /**
     * 交易单号
     */
    private String tradeNum;
    
    /**
     * 手续费
     * @author peng
     */
    @Data
    public static class FeeItem implements Serializable{
        
        /**
         * 
         */
        private static final long serialVersionUID = -1857948491810811398L;

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
        
        /**
         * 0=买，1=卖
         */
        private int type;
    }
}
