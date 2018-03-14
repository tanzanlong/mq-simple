package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateAccountInfo implements Serializable{
    
    private static final long serialVersionUID = 2153667722729946630L;

    //业务编码
    private String businessType;
    
    //证件类型
    private String idType;
    
    //证件编码
    private String idCode;
    
    //姓名
    private String name;
    
    //用户ID
    private String userId;
    
}
