package com.baibei.accountservice.paycenter.dto.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OrgFeeDetailQuery implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -6341811608193891841L;
    Date startDate;
    Date endDate;
    List<String> orgIds;
}
