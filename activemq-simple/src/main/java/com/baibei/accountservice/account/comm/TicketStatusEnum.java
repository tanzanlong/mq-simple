package com.baibei.accountservice.account.comm;

public enum TicketStatusEnum {

    INIT("INIT" ,"初始化"),
    GIVED("GIVED", "已赠送"),
    EXPIRED("EXPIRED", "已过期"),
    USED("USED", "已使用"),
    DELETED("DELETED", "已回收");
    
    private String code;
    private String name;
    
    private TicketStatusEnum(String code, String name){
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
