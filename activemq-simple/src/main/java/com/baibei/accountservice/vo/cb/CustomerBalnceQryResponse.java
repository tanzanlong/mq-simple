package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 资金查询响应
 * @author peng
 */
@Data
@ToString
public class CustomerBalnceQryResponse implements Serializable {
    
    private static final long serialVersionUID = -545823564550507447L;

    //用户ID
    private String userId;
    
    //可用余额
    private Long avaliableAmount;
    
    //冻结金额
    private Long freezeAmount;
    
}
