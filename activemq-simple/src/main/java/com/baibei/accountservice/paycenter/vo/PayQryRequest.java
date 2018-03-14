package com.baibei.accountservice.paycenter.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 出入金流水查询请求
 * @author peng
 */
@Data
@ToString
public class PayQryRequest implements Serializable{
    
    private static final long serialVersionUID = 8753638949001410513L;

    //用户ID列表
    private List<String> userIds;
    
    //开始时间
    private Date startTime;
    
    //结束时间
    private Date endTime;
    
    //出入金类型，可空
    private String type;
}
