package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 券赠送请求
 * @author peng
 */
@Data
@ToString
public class TicketGive implements Serializable {

    private static final long serialVersionUID = 155323069773594101L;

    private String businessType;
    
    private String ownerUserId;
    
    private String receiveUserId;
    
    private List<Item> itemList;
    
    @Data
    @ToString
    public static class Item {
        private String ticketType;
        private Integer amount;
        private Date effectiveTime;
        private Date expireTime;
    }
}
