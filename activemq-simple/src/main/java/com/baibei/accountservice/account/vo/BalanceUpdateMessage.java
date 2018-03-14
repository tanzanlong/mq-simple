package com.baibei.accountservice.account.vo;

import lombok.Data;

/**
 * 余额变更消息
 * @author peng
 */
@Data
public class BalanceUpdateMessage {

    //消息ID
    private String messageId;
    
    //用户ID
    private String userId;
    
    //余额类型
    private String blanceType;
    
    //账户ID
    private Long accountId;
    
    //金额
    private Long amount;
}
