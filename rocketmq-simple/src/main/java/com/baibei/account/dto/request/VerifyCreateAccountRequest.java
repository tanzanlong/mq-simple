package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class VerifyCreateAccountRequest implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 7678936025128958370L;

    /**
     * 是否机构
     */
    private Boolean ifOrg ;
    
    /**
      * 用户姓名或者机构名称
      */
    private String realName;

    /**
     * 身份证号 或者机构编码
     */
    private String idCode;
}
