package com.baibei.accountservice.paycenter.dto.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class OrgAssertQuery implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -4382285373516530754L;
    List<String> orgIds;
    Date startTime;
    Date endTime;
}
