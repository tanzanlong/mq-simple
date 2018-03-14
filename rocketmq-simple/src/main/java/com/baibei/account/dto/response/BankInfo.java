package com.baibei.account.dto.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class BankInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4863315295871749762L;

    private Long id;

    private String bankNo;

    private String bankName;

}
