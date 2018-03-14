package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * 券回收请求
 * @author peng
 */
@Data
@ToString
public class TicketDelete implements Serializable {

    private static final long serialVersionUID = -146216930055134953L;
    
    private String businessType;
    
    private String userId;
    
    private String ticketType;
}
