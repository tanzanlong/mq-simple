package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 客户收支汇总请求
 * @author peng
 */
@Data
@ToString
public class RechargeWitndrawOrderSumResponse implements Serializable {

    private static final long serialVersionUID = -7689231591527967502L;

    //总金额
    private Long totalAmount;
    
    //成功金额
    private Long successAmount;
    
    //失败金额 
    private Long failAmount;
    
    //处理中金额
    private Long doingAmount;
}
