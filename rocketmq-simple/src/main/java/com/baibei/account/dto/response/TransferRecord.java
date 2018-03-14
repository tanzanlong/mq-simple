package com.baibei.account.dto.response;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by keegan on 11/05/2017.
 */
@Data
public class TransferRecord implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4368735425600935073L;

    /**
     * 用户ID
     */
    private String userId;


    /**
     * 类型,IN:入金; OUT:出金
     */
    private String type;
    
    /**
     * 流水号
     */
    private String orderId;

    /**
     * 金额(分)
     */
    private Long amount;

    /**
     * 处理状态,DOING:处理中; SUCCESS:成功; FAIL:失败
     */
    private String status;

    /**
     * 存管银行
     */
    private String signedBank;

    /**
     * 存管账号
     */
    private String signedAccount;

    /**
     * 创建时间
     */
    private Date createTime;
    
}
