package com.baibei.accountservice.vo.ch;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class XmodeCustomerRegInfo implements Serializable {

    private static final long serialVersionUID = -4049029047932477629L;

    //姓名
    private String name;
    
    //证件类型
    private String idType;
    
    //证件编码
    private String idCode;
    
    //手机号码
    private String phone;
 
    //业务类型
    private String businessType;
    
    //银行编码
    private String bankCode;
    
    //银行卡号
    private String bankCard;
    
    //用户ID
    private String userId;
    
    //用户直属会员或机构编码(如001)
    private String orgId;
    
}
