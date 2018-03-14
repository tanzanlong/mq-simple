package com.baibei.accountservice.vo.ch;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * Created by octopus on 2017/5/22.
 */
@Data
public class GoodsInformation implements Serializable{

    private static final long serialVersionUID = -5247005500498196403L;

    private Long goodsInfoId;

    private String goodsName;

    private String goodsNo;

    private String goodsType;

    private String centerNo;

    private String marketNo;

    private String goodsFormat;

    private String openOrderPoundageRate;

    private String openOrderPoundageAmount;

    private String closeOrderPoundageRate;

    private String closeOrderpoundageaAmount;

    private String warehousing;

    private String lateFee;

    private String takeGoodsFee;

    private String goodsStatus;
    
    /**
     * 此字段用于标识add,update,不存入数据库!!!
     */
    private String type;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date marketTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date delistingTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date openMarketTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date closeMarketTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}
