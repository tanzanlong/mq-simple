package com.baibei.accountservice.paycenter.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.accountservice.dao.TAbnormalOrderLogMapper;
import com.baibei.accountservice.model.TAbnormalOrderLog;
import com.baibei.accountservice.model.TAbnormalOrderLogExample;
import com.baibei.accountservice.paycenter.dto.request.CorrectAuditQuery;
import com.baibei.accountservice.paycenter.dto.response.CorrectAuditTask;
import com.baibei.accountservice.paycenter.provider.PaycenterCorrectProvider;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class PaycenterCorrectProviderImpl implements PaycenterCorrectProvider {

    @Autowired
    TAbnormalOrderLogMapper tAbnormalOrderLogMapper;
    
    @Override
    public PageResponse<List<CorrectAuditTask>> queryPaycenterCorrectTask(
            CorrectAuditQuery correctAuditQuery) {

        TAbnormalOrderLogExample tAccountCashierLogExample = new TAbnormalOrderLogExample();
        
        TAbnormalOrderLogExample.Criteria criteria = tAccountCashierLogExample.createCriteria();
        if (!StringUtils.isBlank(correctAuditQuery.getOrderId())) {
            criteria
                    .andOrderIdEqualTo(correctAuditQuery.getOrderId());
        }
        if (!StringUtils.isBlank(correctAuditQuery.getAuditStatus())) {
            
            if("NOT_INIT".equals(correctAuditQuery.getAuditStatus())){
                criteria.andAuditStatusNotEqualTo("INIT");
            }else{
                criteria
                .andAuditStatusEqualTo(correctAuditQuery.getAuditStatus()); 
            }
            
          
        }
        
        PageResponse<List<CorrectAuditTask>> cPageResponse = new PageResponse<List<CorrectAuditTask>>();
        Page<Object> page =
                PageHelper.startPage(correctAuditQuery.getCurrentPage(),
                        correctAuditQuery.getPageSize());
        List<TAbnormalOrderLog> list =
                tAbnormalOrderLogMapper.selectByExample(tAccountCashierLogExample);
        List<CorrectAuditTask> correctAuditTasks = new ArrayList<CorrectAuditTask>(list == null ? 0 : list.size());
        for (int i = 0; i < list.size(); i++) {
            TAbnormalOrderLog tAccountCashierLog = list.get(i);
            CorrectAuditTask correctAuditTask = new CorrectAuditTask();
            correctAuditTask.setAuditStatus(tAccountCashierLog.getAuditStatus());
            correctAuditTask.setBankCode(tAccountCashierLog.getChannel());
            correctAuditTask.setChannelOrderId(tAccountCashierLog.getOrderId());
            correctAuditTask.setCreateTime(tAccountCashierLog.getCreateTime());
            correctAuditTask.setErrorType("");
            correctAuditTask.setOrderId(tAccountCashierLog.getOrderId());
            correctAuditTask.setOrderType(tAccountCashierLog.getOrderType());
            correctAuditTask.setOrgAmount(tAccountCashierLog.getOrgAmount());
            correctAuditTask.setOrgStatus(tAccountCashierLog.getOrgStatus());
            correctAuditTask.setChannelAmount(tAccountCashierLog.getChannelAmount());
            correctAuditTask.setChannelStatus(tAccountCashierLog.getChannelStatus());
            correctAuditTasks.add(correctAuditTask);
        }
        cPageResponse.setCurrentPage(correctAuditQuery.getCurrentPage());
        cPageResponse.setData(correctAuditTasks);
        cPageResponse.setPageSize(correctAuditQuery.getPageSize());
        cPageResponse.setTotal(page == null ? 0 : page.getTotal());
        return cPageResponse;
    }

    @Override
    public String auditCorrectTask(String orderId, String auditStatus) {
        String isSuccess="false";
        TAbnormalOrderLogExample tAbnormalOrderLogExample=new TAbnormalOrderLogExample();
        if(orderId==null||"".equals(orderId.trim())){
            return isSuccess;
        }
        if(auditStatus==null||"".equals(auditStatus.trim())){
            return isSuccess;
        }
        tAbnormalOrderLogExample.createCriteria().andOrderIdEqualTo(orderId);
        List<TAbnormalOrderLog> tAbnormalOrderLogs= tAbnormalOrderLogMapper.selectByExample(tAbnormalOrderLogExample);
        TAbnormalOrderLog TAbnormalOrderLog=tAbnormalOrderLogs.get(0);
        TAbnormalOrderLog.setAuditStatus(auditStatus);
        if(tAbnormalOrderLogMapper.updateByPrimaryKey(TAbnormalOrderLog)>0){
            isSuccess="true";
        }
        return isSuccess;
    }
}
