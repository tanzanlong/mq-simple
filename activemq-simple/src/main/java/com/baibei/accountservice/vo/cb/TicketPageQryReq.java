package com.baibei.accountservice.vo.cb;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TicketPageQryReq {

    private String userId;
    
    private Integer pageNo;
    
    private Integer pageSize;
    
    private int status = 1;
}
