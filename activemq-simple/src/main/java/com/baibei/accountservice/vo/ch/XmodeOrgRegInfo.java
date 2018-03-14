package com.baibei.accountservice.vo.ch;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class XmodeOrgRegInfo implements Serializable {
    
    private static final long serialVersionUID = -7962741352349144615L;

    //机构名称（公司名）
    private String name;
    
    //证件类型
    private String idType;
    
    //证件编码
    private String idCode;
    
    //角色
    private Integer role;
 
    //业务类型
    private String businessType;
    
    //银行编码
    private String bankCode;
    
    //银行卡号
    private String bankCard;
    
    //证件类型
    private String userId;
    
    //会员编号或机构编号，如001
    private String orgId;
    
    //会员名称或机构名称
    private String orgName;
    
    //手机号
    private String phone;
}
