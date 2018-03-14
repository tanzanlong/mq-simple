package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class BankInfoSearch implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5068080560296524616L;

    private String bankNo;

    private String bankName;
}
