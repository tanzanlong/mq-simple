package com.baibei.accountservice.paycenter.scheduler.abnormal;

import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.dao.TAbnormalOrderLogMapper;
import com.baibei.accountservice.model.TAbnormalOrderLog;
import com.baibei.accountservice.model.TAbnormalOrderLogExample;

@EnableScheduling
public class AbnormalOrderHandleScheudler {

    @Autowired
    private SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    private TAbnormalOrderLogMapper tAbnormalOrderLogMapper;

    Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


    // 每隔5分钟，扫描一次
    @Scheduled(cron = "0 */10 * * * ?")
    public void handle() {
        if(schedulerMasterCheck.isMaster()){
            for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
                String exchangeTag = entry.getKey();
                DateSourceLocal.setExchangeTag(exchangeTag);
                doHandle() ;
            }
        }
    }


    private void doHandle() {
        TAbnormalOrderLogExample example = new TAbnormalOrderLogExample();
        List<TAbnormalOrderLog> list = tAbnormalOrderLogMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            for(TAbnormalOrderLog tAbnormalOrderLog : list){
                if("IN".equalsIgnoreCase(tAbnormalOrderLog.getOrderType())){//入金
                    //渠道状态为成功，系统状态为失败(长款)
                    if(Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus()) && Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus())){

                    }
                }else if("OUT".equalsIgnoreCase(tAbnormalOrderLog.getOrderType())){//出金
                    //渠道状态为失败，系统状态为成功（长款）
                    if(Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus()) && Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus())){

                    }
                }
            }
        }
    }
}
