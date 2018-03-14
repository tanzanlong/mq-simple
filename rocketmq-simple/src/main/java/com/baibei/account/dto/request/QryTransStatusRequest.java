package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class QryTransStatusRequest implements Serializable {

    private static final long serialVersionUID = 3042075759852619765L;
    
    /**交易流水**/
    private String transNum;

}
