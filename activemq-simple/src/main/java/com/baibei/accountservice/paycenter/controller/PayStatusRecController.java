package com.baibei.accountservice.paycenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.paycenter.bussiness.RechargeBusiness;
import com.baibei.accountservice.paycenter.bussiness.WithdrawBusiness;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.vo.response.RechargeNotify;
import com.baibei.accountservice.paycenter.vo.response.WithdrawNotify;

import lombok.extern.slf4j.Slf4j;

/**
 * 出入金状态接收接口，接收支付网送推送的出入金结果
 * @author peng
 *
 */
@RestController
@RequestMapping("/account/pay")
@Slf4j
public class PayStatusRecController {
    
    @Autowired
    RechargeBusiness rechargeBusiness;
    
    @Autowired
    WithdrawBusiness withdrawBusiness;
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;

    /**
     * 入金结果接收
     * @param rechargeNotify
     * @return
     */
    @RequestMapping(value = "/recRechageNotify")
    public String recRechageNotify(@RequestBody RechargeNotify rechargeNotify){
        String result = "FAIL";
        log.info("recRechageNotify {}", rechargeNotify);
        try{
            //1 验签
            String appKey = dailyTaskConfig.getPaycenterAppKey();
            if(!rechargeNotify.checkSign(appKey)){
                log.warn("check sign fail");
                return result;
            }
            
            //2 更新状态
            rechargeBusiness.updateRechargeOrderStatus(rechargeNotify.getOrderId(), rechargeNotify.getOrderStatus());
            result = "OK";
            
            //3 通知业务系统
            try{
                rechargeBusiness.asyncNotify(rechargeNotify.getOrderId());
            }catch(Exception unused){
                log.warn(unused.getMessage());
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return result;
    }
    
    /**
     * 出金结果接收
     * @param rechargeNotify
     * @return
     */
    @RequestMapping(value = "/recWithdrawNotify")
    public String recWithdrawNotify(@RequestBody WithdrawNotify withdrawNotify){
        String result = "FAIL";
        log.info("recWithdrawNotify {}", withdrawNotify);
        try{
            //1 验签
            String appKey = dailyTaskConfig.getPaycenterAppKey();
            if(!withdrawNotify.checkSign(appKey)){
                log.warn("check sign fail");
                return result;
            }
            
            //2 更新状态
            withdrawBusiness.updateWithdrawOrderStatus(withdrawNotify.getOrderId(), withdrawNotify.getOrderStatus());
            result = "OK";
            
            //3 通知业务系统
            try{
                withdrawBusiness.asyncNotify(withdrawNotify.getOrderId());
            }catch(Exception unused){
                log.warn(unused.getMessage());
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return result;
    }
}


