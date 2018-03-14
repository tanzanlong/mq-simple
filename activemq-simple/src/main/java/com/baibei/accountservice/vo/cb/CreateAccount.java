package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateAccount implements Serializable{
    
    private static final long serialVersionUID = 4241726662141123L;

    //卡号
    private String bankCard;
    
    //银行编号
    private String bankCode;
    
    //业务编码
    private String businessType;
    
    //证件类型
    private String idType;
    
    //证件编码
    private String idCode;
    
    //手机号码
    private String phone;
    
    //姓名
    private String name;
    
    //用户ID
    private String userId;
    
    //用户直属机构编码，如001
    private String orgId;
    
    //用户直属机构名称
    private String orgName;
}
