package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateOrgAccount implements Serializable{
    
    private static final long serialVersionUID = 87453602343922323L;

    //银行编码
    private String bankCode;
    
    //卡号
    private String bankCard;
    
    //持卡人姓名
    private String name;
    
    //证件类型
    private String idType;
    
    //证件名称
    private String idCode;
    
    //手机号
    private String phone;
    
    //用户ID
    private String userId;
    
    //业务类型
    private String businessType;
    
    //会员编码
    private String orgId;
    
    //会员名称或机构名称
    private String orgName;
    
    //角色
    private Integer role;
}
