package com.baibei.accountservice.paycenter.utill;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.dto.base.BaseResponse;
import com.baibei.accountservice.paycenter.dto.response.RechargeResponse;
import com.baibei.accountservice.paycenter.dto.response.WithdrawResponse;

@Component
@Slf4j
public class PayRestfulUtil {
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;
    
    public BaseResponse<RechargeResponse> recharge(TRechargeWithdrawOrder tRechargeOrder) {
        String userId = tRechargeOrder.getUserId();
        Long accountId = tRechargeOrder.getAccountId();
        Long amount = tRechargeOrder.getAmount();
        String orderId = tRechargeOrder.getOrderId();
        BaseResponse<RechargeResponse> response=new BaseResponse<RechargeResponse>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        RechargeRequet rechargeRequet = new RechargeRequet();
        rechargeRequet.setAccountId(accountId);
        rechargeRequet.setAmount(amount);
        rechargeRequet.setOrderId(orderId);
        rechargeRequet.setUserId(userId);
        rechargeRequet.setBusinessType("CH");
        log.info("  PayRestfulUtil recharge RechargeRequet:{}",JSON.toJSONString(rechargeRequet));
        HttpEntity<RechargeRequet> formEntity =
                new HttpEntity<RechargeRequet>(rechargeRequet, headers);
        String result =
                restTemplate.postForObject(
                        String.format("%s/%s", dailyTaskConfig.getSettlementPaycenterBaseUrl()+"/chrecharge", "/dorecharge"),
                        formEntity, String.class);
        log.info("  PayRestfulUtil recharge RechargeResponse :{}",JSON.toJSONString(result));
        if(result!=null&&!result.trim().equals("")){
            JSONObject responseJson=JSON.parseObject(result);
            RechargeResponse rechargeResponse=new RechargeResponse();
            Integer rc=responseJson.getInteger("rc")==null?-1:responseJson.getInteger("rc");
            response.setRc(rc);
            response.setMsg(responseJson.getString("msg"));
            if(BaseResponse.RC_SUCCESS==rc){
                rechargeResponse=JSON.parseObject(responseJson.getString("data"), RechargeResponse.class);
            }
            response.setData(rechargeResponse);
        }
        return response;
    }


    
    public BaseResponse<WithdrawResponse> withdraw(String userId,Long accountId,Long amount,String orderId) {
        BaseResponse<WithdrawResponse> response=new BaseResponse<WithdrawResponse>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAccountId(accountId);
        withdrawRequest.setAmount(amount);
        withdrawRequest.setOrderId(orderId);
        withdrawRequest.setUserId(userId);
        withdrawRequest.setBusinessType("CH");
        log.info("  PayRestfulUtil recharge RechargeRequet:{}",JSON.toJSONString(withdrawRequest));
        HttpEntity<WithdrawRequest> formEntity =
                new HttpEntity<WithdrawRequest>(withdrawRequest, headers);
        String result =
                restTemplate.postForObject(
                        String.format("%s/%s", dailyTaskConfig.getSettlementPaycenterBaseUrl()+"/chwithdraw", "/dowithdraw"),
                        formEntity, String.class);
        log.info("  PayRestfulUtil recharge WithdrawResponse :{}",JSON.toJSONString(result));
        if(result!=null&&!result.trim().equals("")){
            JSONObject responseJson=JSON.parseObject(result);
            WithdrawResponse withdrawResponse=new WithdrawResponse();
            Integer rc=responseJson.getInteger("rc")==null?-1:responseJson.getInteger("rc");
            response.setRc(rc);
            response.setMsg(responseJson.getString("msg"));
            if(BaseResponse.RC_SUCCESS==rc){
                withdrawResponse=JSON.parseObject(responseJson.getString("data"), WithdrawResponse.class);
            }
            response.setData(withdrawResponse);
        }
        return response;
    }

    
    

    public static class RechargeRequet implements Serializable{
        private static final long serialVersionUID = -8711414546103656491L;

        @Getter
        @Setter
        private String userId;

        @Getter
        @Setter
        private Long accountId;

        @Getter
        @Setter
        private Long amount;

        @Getter
        @Setter
        private String orderId;
        @Getter
        @Setter
        private String businessType;

    }
    
    
    public static class WithdrawRequest implements Serializable{
        private static final long serialVersionUID = -3045263182031045072L;

        @Getter
        @Setter
        private String businessType;
        
        @Getter
        @Setter
        private String userId;
        
        @Getter
        @Setter
        private Long accountId;
        
        @Getter
        @Setter
        private Long amount;
        
        @Getter
        @Setter
        private String orderId;
}
    
}
