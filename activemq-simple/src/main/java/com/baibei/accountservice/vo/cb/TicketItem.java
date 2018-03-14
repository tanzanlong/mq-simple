package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TicketItem implements Serializable {

    private static final long serialVersionUID = 1394487940685456012L;

    private String id;
    
    private String ticketName;
    
    private String ticketType;
    
    private Long ticketValue;
    
    private Integer ticketFaceValue;
    
    private Date effectiveTime;
    
    private Date expireTime;
    
    private String ticketStatus;
    
}
