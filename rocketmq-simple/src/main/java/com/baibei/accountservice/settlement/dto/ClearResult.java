package com.baibei.accountservice.settlement.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ClearResult  implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 4762381001780467945L;

    /**
     * 日期
     */
    private String clearDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 时间
     */
    private String time;

}
