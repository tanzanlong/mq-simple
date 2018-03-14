package com.baibei.accountservice.account.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ValidatePasswordResult {

    //连续输入错误次数
    private int errorCount;
    
    //剩余次数
    private int leftCount;
    
    //验证结果
    private boolean validateResult;
}
