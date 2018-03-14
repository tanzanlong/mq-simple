package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CbChangePasswordReq implements Serializable {
   
    private static final long serialVersionUID = 4328433561424855274L;

    private String businessType;
    
    private String userId;
    
    private String oldPassword;
    
    private String newPassword;
  
}
