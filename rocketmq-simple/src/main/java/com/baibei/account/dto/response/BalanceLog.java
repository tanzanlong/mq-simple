package com.baibei.account.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.baibei.account.enums.BalanceTypeEnum;
import com.baibei.account.enums.FeeItemEnum;
import com.baibei.account.enums.OrderTypeEnum;
import com.baibei.accountservice.paycenter.common.Constants;

import lombok.Data;

@Data
public class BalanceLog implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -6143951660032080478L;

    /**
     * 订单流水号 
     */
    private String orderId;

    /**{@link OrderTypeEnum}订单类型
     * 订单类型 
     */
    private String orderType;
    
    /**{@link OrderTypeEnum}
     * 订单类型描述  
     */
    private String orderTypeDes;

    /**
     * 用户类型
     */
    private String userId;

    /**
     * 金额
     */
    private Long changeAmount;

    /**
     * 变动前金额
     */
    private Long changeBefore;

    /**{@link BalanceTypeEnum}余额类型
     * 余额类型
     */
    private String balanceType;

    /**
     * 余额类型描述  
     */
    private String balanceTypeDes;
    
    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * 费用类型 {@link FeeItemEnum}费用类型
     */
    private String feeItem;
    
    /**
     * 费用类型描述  
     */
    private String feeItemDes;

    /**
     * 机构编码
     */
    private String orgId;
}
