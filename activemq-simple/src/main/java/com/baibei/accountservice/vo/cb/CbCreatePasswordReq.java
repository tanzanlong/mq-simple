package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CbCreatePasswordReq implements Serializable {
   
    private static final long serialVersionUID = -5211218888664116880L;

    private String businessType;
    
    private String userId;
    
    private String password;
  
}
