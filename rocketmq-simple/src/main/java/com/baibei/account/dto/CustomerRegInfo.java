package com.baibei.account.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
public class CustomerRegInfo implements Serializable {
    
    private static final long serialVersionUID = -362818368081370489L;

    @Setter
    @Getter
    private String name;
    
    @Setter
    @Getter
    private String topOrgId;
    
    @Setter
    @Getter
    private String idCode;
    
    @Setter
    @Getter
    private String mobile;
 
    @Setter
    @Getter
    private String businessType;
    
    @Setter
    @Getter
    private String bankCode;
    
    @Setter
    @Getter
    private String bankCard;
    
    @Setter
    @Getter
    private String userId;
    @Setter
    @Getter
    private String idType;
    @Setter
    @Getter
    private String password;
    
}
