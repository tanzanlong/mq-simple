package com.baibei.accountservice.vo.cb;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 券入库请求
 * @author peng
 */
@Data
@ToString
public class TicketInStore implements Serializable {

    private static final long serialVersionUID = -146216930055134953L;
    
    private String businessType;
    
    private String userId;
    
    private List<Item> itemList;
    
    @Data
    @ToString
    public static class Item {
        private String ticketName;
        private String ticketType;
        private Long ticketValue;
        private Integer ticketFaceValue;
        private Integer amount;
    }
}
