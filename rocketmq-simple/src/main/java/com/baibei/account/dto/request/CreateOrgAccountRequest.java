package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 机构用户开户请求
 * Created by keegan on 11/05/2017.
 */
@Data
public class CreateOrgAccountRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8218859355006610785L;
    
    /**清算中心：广清**/
    public static String TYPE_CLEARCENTER = "CLEARCENTER";
    
    /**
     * 交易中心
     */
    public static String TYPE_CENTER = "CENTER";
    /**
     * 经纪商
     */
    public static String TYPE_BROKER = "BROKER";
    /**
     * 交易基金
     */
    public static String TYPE_SPECIAL = "SPECIAL";
    /**
     * 采购商
     */
    public static String TYPE_DIRECT = "PROCUREMENT";
    /**
     * 产业基金
     */
    public static String TYPE_INDUSTRY = "INDUSTRY";
    /**
     * IT公司
     */
    public static String TYPE_IT_COMPANY = "IT_COMPANY";
    
    public static Integer getSettlementOrgType(String orgType){
        /**
         *  201=广清
            202=交易中心
            203=经纪商
            204=交易基金
            205=采购商
            206=产业基金
            207=IT公司
         */
        if(TYPE_CLEARCENTER.equalsIgnoreCase(orgType)){
            return 201;
        }else if(TYPE_CENTER.equalsIgnoreCase(orgType)){
            return 202;
        }else if(TYPE_BROKER.equalsIgnoreCase(orgType)){
            return 203;
        }else if(TYPE_SPECIAL.equalsIgnoreCase(orgType)){
            return 204;
        }else if(TYPE_DIRECT.equalsIgnoreCase(orgType)){
            return 205;
        }else if(TYPE_INDUSTRY.equalsIgnoreCase(orgType)){
            return 206;
        }else if(TYPE_IT_COMPANY.equalsIgnoreCase(orgType)){
            return 207;
        }else{
            return 0;
        }
    }

    /**
     * 机构用户ID
     */
    private String userId;

    /**
     * 机构类型
     */
    private String orgType;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 统一信用代码
     */
    private String idCode;

    /**
     * 手机号
     */
    private String mobile;
}
