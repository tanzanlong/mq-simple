package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 出入金手续费请求
 * @author peng
 */
@Data
@ToString
public class FeeItemRequest implements Serializable {

    private static final long serialVersionUID = 2720641145940940621L;

    //用户ID
    private String userId;
    
    //金额
    private Long fee;
    
    //账户ID，冗余
    private Long accountId;
}
