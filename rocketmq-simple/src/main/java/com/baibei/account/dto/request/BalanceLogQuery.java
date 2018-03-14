package com.baibei.account.dto.request;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baibei.accountservice.paycenter.dto.PageBaseRequest;

@Data
@EqualsAndHashCode(callSuper=true)
public class BalanceLogQuery extends PageBaseRequest  implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -2983406052425228300L;
    /**
     * 用户id
     */
    private String userId;
}
