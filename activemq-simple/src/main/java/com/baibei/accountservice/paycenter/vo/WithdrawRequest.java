package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 出金请求
 * @author peng
 */
@Data
@ToString
public class WithdrawRequest implements Serializable {

    private static final long serialVersionUID = -7138455895017734929L;

    //订单号
    private String orderId;
    
    //收款银行编码
    private String bank;
    
    //收款账户号
    private String bankAccount;
    
    //真实姓名
    private String realName;
    
    //支行信息
    private String branchBankName;
    
    //金额
    private Long amount;
    
    //业务系统
    private String businessType;
    
    //省份
    private String province;
    
    //城市
    private String city;
    
    //渠道
    private String channelCode;
    
    //用户ID
    private String userId;
    
    //手续费列表
    private List<FeeItemRequest> feeItemList;
    
    //账户ID，冗余
    private Long accountId;
}
