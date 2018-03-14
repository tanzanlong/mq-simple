package com.baibei.account.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
public class RegInfoResponse implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -8396134123412209130L;

    @Setter
    @Getter
    private Long accountId;
    
}
