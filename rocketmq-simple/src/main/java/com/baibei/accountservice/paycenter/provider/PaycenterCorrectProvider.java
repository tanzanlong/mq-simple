package com.baibei.accountservice.paycenter.provider;

import java.util.List;

import com.baibei.account.dto.response.PageResponse;
import com.baibei.accountservice.paycenter.dto.request.CorrectAuditQuery;
import com.baibei.accountservice.paycenter.dto.response.CorrectAuditTask;


public interface PaycenterCorrectProvider {

    /**
     * @param correctAuditQuery
     * @return
     */
    public PageResponse<List<CorrectAuditTask>> queryPaycenterCorrectTask(
            CorrectAuditQuery correctAuditQuery);
    
    
    /**
     * @param orderId
     * @param status
     * @return
     */
    public String auditCorrectTask(String orderId,String status);

}
