package com.baibei.accountservice.paycenter.controller.ch.notifyrec;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baibei.accountservice.paycenter.bussiness.ch.WithdrawBussiness;
import com.baibei.accountservice.paycenter.vo.response.WithdrawNotify;

/**
 * 入金订单状态接收，用于渠道异步通知入金结果
 * 
 * @author tan
 */
@Controller
@RequestMapping("/account/withdraw")
@Slf4j
public class WithdrawStatusRecController {
    @Autowired
    WithdrawBussiness withdrawBussiness;

    @RequestMapping("/asynNotify")
    @ResponseBody
    public String recvCjPay(HttpServletRequest req, @RequestBody WithdrawNotify withdrawNotify) {
        log.info("WithdrawStatusRecController orderId:{} ", withdrawNotify == null ? ""
                : withdrawNotify.getOrderId());
        if(withdrawNotify==null){
            return "error";
        }
        return withdrawBussiness.asyStatusUpdate(withdrawNotify);
    }
}
