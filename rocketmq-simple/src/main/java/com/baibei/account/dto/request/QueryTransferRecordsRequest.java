package com.baibei.account.dto.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Created by keegan on 11/05/2017.
 */
@Data
public class QueryTransferRecordsRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -8195120384429407378L;

    /**
     * 用户ID列表
     */
    private List<String>  userIds;

    /**
     * 用户ID列表
     */
    private List<String> orgIds;
    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 类型,IN:入金;OUT:出金
     */
    private String type;

    /**
     * 处理状态,DOING:处理中; SUCCESS:成功; FAIL:失败
     */
    private String status;
    
    
    /**
     * 订单号
     */
    private String orderId;

    /**
     * 每页条数
     */
    private int pageSize = 10;

    /**
     * 查询页数
     */
    private int currentPage = 1;
}
