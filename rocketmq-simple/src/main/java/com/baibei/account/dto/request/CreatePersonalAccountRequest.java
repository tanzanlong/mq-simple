package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 个人用户开户请求
 * Created by keegan on 11/05/2017.
 */
@Data
public class CreatePersonalAccountRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5688976663720891108L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 所属会员ID
     */
    private String topOrgId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCode;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 银行编码
     */
    private String bankCode;

    /**
     * 银行卡号
     */
    private String bankCard;

    /**
     * 资金密码
     */
    private String password;
}
