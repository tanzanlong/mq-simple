package com.baibei.accountservice.paycenter.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.paycenter.bussiness.PayCenterAbnormalOrderBusiness;
import com.baibei.accountservice.paycenter.dto.NotifyAbonormalOrderRequest;

/**
 * 入金订单状态查询接口
 * @author tan
 */
@RestController
@RequestMapping("/account/paycenter")
public class PayCenterAbnormalOrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(PayCenterAbnormalOrderController.class);
    
    @Autowired
    PayCenterAbnormalOrderBusiness payCenterAbnormalOrderBusiness;

    @RequestMapping(value = "/asyNotifyAbornormal")
    public String asyNotifyAbonormalOrder(HttpServletRequest request,@RequestBody NotifyAbonormalOrderRequest notifyAbonormalOrderRequest) {
        String isOk="notOk";
        try{
            logger.info(" PayCenterAbnormalOrderController  NotifyAbonormalOrderRequest:{}",JSON.toJSONString(notifyAbonormalOrderRequest));
            isOk=payCenterAbnormalOrderBusiness.notifyAbnormalOrder(notifyAbonormalOrderRequest);
        }catch(Exception e){
            logger.error("PayCenterAbnormalOrderController asyNotifyAbonormalOrder :{}",e);
        }
        return isOk;
    }
    
}
