package com.baibei.accountservice.vo.ch;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Created by octopus on 2017/5/22.
 */
@Data
public class MarketInformation implements Serializable{

    private static final long serialVersionUID = -7848594848580997341L;

    private Long marketInfoId;

    private String marketName;

    private String marketNo;

    private String centerNo;

    private String tradeModel;

    private String marketStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date openMarketTime1;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date closeMarketTime1;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date openMarketTime2;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date closeMarketTime2;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date settleTime;

    private String settleModel;

    private String settlePeroid;

    private String approvalStatus;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date approvalTime;

    private String approvalMsg;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
