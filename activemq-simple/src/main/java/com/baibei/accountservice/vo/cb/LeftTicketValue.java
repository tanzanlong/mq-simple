package com.baibei.accountservice.vo.cb;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LeftTicketValue implements Serializable {

    private static final long serialVersionUID = 6276270080738521572L;
    
    private Long usedTicketValue;
    
    private Long ticketBonds;

}
