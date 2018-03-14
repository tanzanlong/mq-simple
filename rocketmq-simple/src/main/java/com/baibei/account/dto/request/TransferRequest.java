package com.baibei.account.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by keegan on 12/05/2017.
 */
@Data
public class TransferRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1162473666250674749L;
    /**
     * 流水号，生成规则：服务调用方标识（2位字母）+时间戳后10位+6位随机数
     */
    private String serialNo;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 入金金额（分）
     */
    private Long amount;
    /**
     * 资金密码
     */
    private String password;
    /**
     * 机构代码
     */
    private String orgId;
}
