package com.baibei.accountservice.paycenter.controller.ch.notifyrec;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baibei.accountservice.paycenter.bussiness.ch.RechargeBussiness;
import com.baibei.accountservice.paycenter.vo.response.RechargeNotify;

/**
 * 入金订单状态接收，用于渠道异步通知入金结果
 * 
 * @author tan
 */
@Controller
@RequestMapping("/account/recharge")
@Slf4j
public class RechargeStatusRecController {
    @Autowired
    RechargeBussiness rechargeBussiness;
	 @RequestMapping("/asynNotify")
	 @ResponseBody
	 public String recvCjPay(HttpServletRequest req, @RequestBody RechargeNotify rechargeNotify) {
		 log.info("RechargeStatusRecController orderId:{} ",rechargeNotify==null?"":rechargeNotify.getOrderId());
	        if(rechargeNotify==null){
	            return "error";
	        }
		 return rechargeBussiness.asyStatusUpdate(rechargeNotify);
	 }
}
