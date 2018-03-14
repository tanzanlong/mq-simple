package com.baibei.accountservice.paycenter.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample.Criteria;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.paycenter.vo.PayQryRequest;
import com.baibei.accountservice.paycenter.vo.PayQryResponse;
import com.baibei.accountservice.util.RspUtils;
import com.ctrip.framework.apollo.core.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 出入金接口
 * @author peng
 *
 */
@RestController
@RequestMapping("/account/payqry")
@Slf4j
public class PayQryController {
    
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    @RequestMapping(value = "/qryrechargestatus/{businessType}/{orderId}")
    public BaseResponse<String> qryrechargestatus(@PathVariable("businessType") String businessType, @PathVariable("orderId") String orderId){
        try{
            TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
            example.createCriteria().andOrderIdEqualTo(orderId).andBusinessTypeEqualTo(businessType).andOrderTypeEqualTo("IN");
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            if(CollectionUtils.isNotEmpty(list)){
                return RspUtils.success(list.get(0).getStatus());
            }else{
                throw new IllegalArgumentException("入金订单不存在");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/qrywithdrawstatus/{businessType}/{orderId}")
    public BaseResponse<String> qrywithdrawstatus(@PathVariable("businessType") String businessType, @PathVariable("orderId") String orderId){
        try{
            TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
            example.createCriteria().andOrderIdEqualTo(orderId).andBusinessTypeEqualTo(businessType).andOrderTypeEqualTo("OUT");
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            if(CollectionUtils.isNotEmpty(list)){
                return RspUtils.success(list.get(0).getStatus());
            }else{
                throw new IllegalArgumentException("出金订单不存在");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/qryrechargewithdrawslist")
    public BaseResponse<List<PayQryResponse>> qryrechargewithdrawslist(@RequestBody PayQryRequest payQryRequest){
        log.info("qryrechargewithdrawslist {}", payQryRequest);
        try{
            //参数判断
            checkParam(payQryRequest);
            
            //查询
            TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
            Criteria criteria = example.createCriteria();
            criteria.andUserIdIn(payQryRequest.getUserIds()).andCreateTimeGreaterThanOrEqualTo(payQryRequest.getStartTime()).andCreateTimeLessThanOrEqualTo(payQryRequest.getEndTime());
            if(!StringUtils.isBlank(payQryRequest.getType())){
                criteria.andOrderTypeEqualTo(payQryRequest.getType());
            }
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            
            //结果转换
            List<PayQryResponse> resultList = new ArrayList<PayQryResponse>();
            if(CollectionUtils.isNotEmpty(list)){
                for(TRechargeWithdrawOrder tRechargeWithdrawOrder : list){
                    PayQryResponse response = toPayQryResponse(tRechargeWithdrawOrder);
                    resultList.add(response);
                }
            }
            return RspUtils.success(resultList);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private PayQryResponse toPayQryResponse(TRechargeWithdrawOrder tRechargeWithdrawOrder){
        PayQryResponse response = new PayQryResponse();
        response.setAmount(tRechargeWithdrawOrder.getAmount());
        response.setOrderId(tRechargeWithdrawOrder.getOrderId());
        response.setStatus(tRechargeWithdrawOrder.getStatus());
        response.setType(tRechargeWithdrawOrder.getOrderType());
        response.setUserId(tRechargeWithdrawOrder.getUserId());
        return response;
    }
    
    private void checkParam(PayQryRequest payQryRequest){
        if(payQryRequest.getEndTime() == null){
            throw new IllegalArgumentException("parameter endTime can not be null");
        }
        if(payQryRequest.getStartTime() == null){
            throw new IllegalArgumentException("parameter startTime can not be null");
        }
        if(CollectionUtils.isEmpty(payQryRequest.getUserIds())){
            throw new IllegalArgumentException("parameter userIds can not be empty");
        }
    }
    
    
}


