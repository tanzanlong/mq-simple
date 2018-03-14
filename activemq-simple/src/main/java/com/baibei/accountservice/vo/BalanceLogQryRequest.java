package com.baibei.accountservice.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BalanceLogQryRequest implements Serializable {

    private static final long serialVersionUID = -2414713231223874039L;

    //用户
    private List<String> userIds;
    
    //开始时间
    private Date startTime;
    
    //结束时间
    private Date endTime;
    
    //业务系统
    private String businessType;
    
    //页码
    private Integer pageNo = 0;
    
    //每页记录数
    private Integer pageSize = 10;
}
