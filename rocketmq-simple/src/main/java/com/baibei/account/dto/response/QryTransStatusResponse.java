package com.baibei.account.dto.response;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QryTransStatusResponse implements Serializable {

    private static final long serialVersionUID = -9222856614081220652L;

    /**交易流水**/
    private String transNum;

    /**交易类型**/
    private String transType; 
    
    /**交易状态: 成功=SUCCESS，失败=FAIL，已回退=CANCELED，不存在=NOTEXISTS**/
    private String transStatus;
}
