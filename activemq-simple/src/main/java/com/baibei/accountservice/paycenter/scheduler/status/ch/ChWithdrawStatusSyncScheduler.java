package com.baibei.accountservice.paycenter.scheduler.status.ch;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.config.SchedulerMasterConfig;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.bussiness.ch.WithdrawBussiness;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.vo.response.WithdrawNotify;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时查询平安出金订单状态并更新到出金订单表，通常用于平安服务器长时间不推送结果，而且订单状态是处理中的情况
 * 
 * @author tan
 */
@Component
@EnableScheduling
@Slf4j
public class ChWithdrawStatusSyncScheduler {

    static final Logger logger = LoggerFactory.getLogger(ChWithdrawStatusSyncScheduler.class);

  /*  @Autowired
    TWithdrawOrderMapper tWithdrawOrderMapper;*/
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    
    @Autowired
    WithdrawBussiness withdrawBussiness;
    
    @Autowired
    SchedulerMasterCheck SchedulerMasterCheck;
    
    @Autowired
    SchedulerMasterConfig schedulerMasterConfig;

    Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


    // 每隔5分钟，扫描一次
    @Scheduled(cron = "0 */10 * * * ?")
    public void updateStatusForPingAnWithdraw() {
        if(SchedulerMasterCheck.isMaster()){
            for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
                String exchangeTag = entry.getKey();
                DateSourceLocal.setExchangeTag(exchangeTag);
                //
                doUpdateStatusForPingAnWithdraw();

            }
        }
    }
   
    private void doUpdateStatusForPingAnWithdraw() {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        Date date = new Date();
        // 1周内到1分钟前
        Date startTime = new Date(date.getTime() - 7*24*3600*1000);
        Date endTime = new Date(date.getTime() - 60 * 1000);
        example.createCriteria().andStatusEqualTo(PayCenterConstant.STATUS_DOING).andCreateTimeGreaterThan(startTime).andCreateTimeLessThan(endTime).andOrderTypeEqualTo("OUT").andBusinessTypeEqualTo("CH");
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        logger.info("updateStatusForPingAnWithdraw list size:{}",list==null?0:list.size());
        if (!CollectionUtils.isEmpty(list)) {
            for(TRechargeWithdrawOrder order : list){
                try{
                    WithdrawNotify withdrawNotify =
                            withdrawBussiness.queryWithdrawStatus(order.getOrderId());
                    withdrawNotify.setOrderStatus(PayCenterConstant.STATUS_SUCCESS);
                    if (PayCenterConstant.STATUS_SUCCESS.equals(withdrawNotify.getOrderStatus())
                            || PayCenterConstant.STATUS_FAIL.equals(withdrawNotify.getOrderStatus())) {// 最终态才处理
                        withdrawBussiness.asyStatusUpdate(withdrawNotify);
                    }
                }catch(Exception e){
                    logger.error(e.getMessage());
                }
            }
        }
    }
}
