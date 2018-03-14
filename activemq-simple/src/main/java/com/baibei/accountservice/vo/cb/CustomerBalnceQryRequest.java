package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 资金查询请求
 * @author peng
 */
@Data
@ToString
public class CustomerBalnceQryRequest implements Serializable {
    
    private static final long serialVersionUID = -545823564550507447L;

    //用户ID
    private List<String> userIdList;
}
