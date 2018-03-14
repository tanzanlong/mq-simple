package com.baibei.accountservice.account.comm;

public enum TransStatusEnum {

    SUCCESS("SUCCESS" ,"成功"),
    CANCELED("CANCELED", "已回退"),
    NOTEXISTS("NOTEXISTS", "不存在");
    
    private String code;
    private String name;
    
    private TransStatusEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
    
    public String getCode(){
        return this.code;
    }
    
    public String getName(){
        return this.name;
    }
}
