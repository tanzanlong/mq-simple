package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Balance implements Serializable {
    
    private static final long serialVersionUID = 2293763692140014437L;
    
    //可用
    private Long canUseAmount;
    
    //冻结
    private Long freezeAmount;
    
    //已结算
    private Long canWithdrawAmount;

}
