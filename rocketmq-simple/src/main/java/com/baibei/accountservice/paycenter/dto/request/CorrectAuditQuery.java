package com.baibei.accountservice.paycenter.dto.request;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.baibei.accountservice.paycenter.dto.PageBaseRequest;

@Data
@EqualsAndHashCode(callSuper=true)
public class CorrectAuditQuery extends PageBaseRequest  implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3690210523991424361L;

    /**
     * 订单流水
     */
    private String orderId;

    /**
     * 状态
     */
    private String auditStatus;
}
