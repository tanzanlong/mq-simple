package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

/**
 * 客户出入金查询请求
 * @author peng
 */
@Data
@ToString
public class RechargeWitndrawOrderQryRequest implements Serializable {

    private static final long serialVersionUID = -7689231591527967502L;

    //用户ID
    private String userId;

    //userIdList
    private List<String> userIdList;

    //机构ID
    private String orgId;
    
    //开始时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    
    //结束时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    
    //订单类型, IN=入金，OUT=出金
    private String orderType;
    
    //订单状态 SUCCESS=成功,FAIL=失败，DOING=处理中
    private String orderStatus;
    
    //订单号
    private String orderId;
    
    //渠道编码
    private String channelCode;

    //业务类型
    private String businessType;

    //页码
    private int pageNo = 0;
    
    //每页记录条数
    private int pageSize = 10;
    
}
