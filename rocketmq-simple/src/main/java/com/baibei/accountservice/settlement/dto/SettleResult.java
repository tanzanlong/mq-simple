package com.baibei.accountservice.settlement.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SettleResult implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7467551103597604010L;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行 编号 
     */
    private String bankCode;

    /**
     * 签退时间
     */
    private Date signOutTime;

    /**
     * 签到时间
     */
    private Date signInTime;

    /**
     * 结算开始时间
     */
    private Date settleStartTime;

    /**
     * 结算结束时间
     */
    private Date settleEndTime;

    /**
     * 结算状态
     */
    private String settleStatus;
    
    private String tradAccount;

    private String tradAmount;

    /**
     * 
     */
    private String bankAccount;


}
