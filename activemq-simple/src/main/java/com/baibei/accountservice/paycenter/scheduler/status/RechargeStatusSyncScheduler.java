package com.baibei.accountservice.paycenter.scheduler.status;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.bussiness.RechargeBusiness;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * 入金状态定时查询 
 * @author peng
 */
@Component
@EnableScheduling
@Slf4j
public class RechargeStatusSyncScheduler {

    @Autowired
    RechargeBusiness rechargeBusiness;
    
    @Autowired
    SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;

    Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();
   
    // 每隔5分钟，扫描一次
    @Scheduled(cron = "0 */1 * * * ?")
    public void updateStatus() {
        if(schedulerMasterCheck.isMaster()){
            for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
                String exchangeTag = entry.getKey();
                DateSourceLocal.setExchangeTag(exchangeTag);
                //
                doUpdateStatus();

            }
        }
    }
   
    private void doUpdateStatus() {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        Date date = new Date();
        // 1周内到1分钟前
        Date startTime = new Date(date.getTime() - 7*24*3600*1000);
        Date endTime = new Date(date.getTime() - 60 * 1000);
        example.createCriteria().andStatusEqualTo(PayCenterConstant.STATUS_DOING).andCreateTimeGreaterThan(startTime).andCreateTimeLessThan(endTime).andOrderTypeEqualTo("IN").andBusinessTypeNotEqualTo("CH");;
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        log.info("updateStatusForPingAnWithdraw list size:{}",list==null?0:list.size());
        if (!CollectionUtils.isEmpty(list)) {
            for(TRechargeWithdrawOrder order : list){
                try{
                    //1 状态查询
                    String status = rechargeBusiness.queryRechargeStatus(order.getBusinessType(), order.getOrderId());
                    if(Constants.STATUS_SUCCESS.equalsIgnoreCase(status) || Constants.STATUS_FAIL.equalsIgnoreCase(status)){
                        //2 更新状态
                        rechargeBusiness.updateRechargeOrderStatus(order.getOrderId(), status);
                        
                        //3 通知业务系统
                        try{
                            rechargeBusiness.asyncNotify(order.getOrderId());
                        }catch(Exception unused){
                            log.warn(unused.getMessage());
                        }
                    }
                }catch(Exception e){
                    log.error(e.getMessage());
                }
            }
        }
    }
}
