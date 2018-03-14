package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * H5入金请求
 * @author peng
 */
@Data
@ToString
public class RechargeH5Request implements Serializable {

    private static final long serialVersionUID = -5379407316721749698L;

    //卡号
    private String bankAccount;
    
    //金额，单位分
    private Long amount;
    
    //订单号
    private String orderId;
    
    //业务系统
    private String businessType;
    
    //渠道
    private String channelCode;
    
    //用户ID
    private String userId;
    
    //支付成功后跳转地址
    private String callbackUrl;
    
    //手续费列表
    private List<FeeItemRequest> feeItemList;
    
    //账户ID，冗余
    private Long accountId;

    //持卡人姓名，中南支付必填
    private String name;

    //手机号，中南支付必填
    private String phone;

    //身份证号，中南支付必填
    private String certNo;
}
