package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 资金查询汇总
 * @author peng
 */
@Data
@ToString
public class CustomerBalnceSumResponse implements Serializable {
    
    private static final long serialVersionUID = -545823564550507447L;

    //总可用余额
    private Long totalAvaliableAmount;
    
    //总冻结金额
    private Long totalFreezeAmount;
    
}
