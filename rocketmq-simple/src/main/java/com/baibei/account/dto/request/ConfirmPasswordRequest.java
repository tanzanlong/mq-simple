package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class ConfirmPasswordRequest implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 7678936025128958370L;

    /**
      * 用户id
      */
    private String userId;

    /**
     * 资金密码
     */
    private String password;
}
