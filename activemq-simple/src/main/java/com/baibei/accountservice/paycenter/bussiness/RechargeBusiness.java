package com.baibei.accountservice.paycenter.bussiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq.Detail;
import com.baibei.accountservice.dao.TRechargeWithdrawFeeitemMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TRechargeWithdrawFeeitem;
import com.baibei.accountservice.model.TRechargeWithdrawFeeitemExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.vo.FeeItemRequest;
import com.baibei.accountservice.paycenter.vo.PayResult;
import com.baibei.accountservice.paycenter.vo.RechargeH5Request;
import com.baibei.accountservice.util.JedisClient;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RechargeBusiness {

    static final String orderType = "IN";
    
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    JedisClient jedisClient;
    
    @Autowired
    TRechargeWithdrawFeeitemMapper tRechargeWithdrawFeeitemMapper;
    
    public TRechargeWithdrawOrder qryRechargeOrderByOrderId(String orderId){
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.createCriteria().andOrderIdEqualTo(orderId).andOrderTypeEqualTo(orderType);
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }
    
    /**
     * 对业务系统异步通知
     * @param orderId
     */
    public void asyncNotify(String orderId){
        TRechargeWithdrawOrder tRechargeWithdrawOrder = qryRechargeOrderByOrderId(orderId);
        if(tRechargeWithdrawOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_SUCCESS) || tRechargeWithdrawOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_FAIL)){//最终状态才通知
            PayResult payResult = new PayResult();
            payResult.setAmount(tRechargeWithdrawOrder.getAmount());
            payResult.setOrderId(tRechargeWithdrawOrder.getOrderId());
            payResult.setOrderStatus(tRechargeWithdrawOrder.getStatus());
            payResult.setExchange(DateSourceLocal.getExchangeTag());
            String message = JSON.toJSONString(payResult);
            String channel = PayCenterConstant.CHANNEL_RECHARGE_RESULT;
            jedisClient.publishMsg(channel, message);
            log.info("publish message {} to channel {} success", message, channel);
        }
    }
    
    /**
     * 更新入金订单状态
     * @param orderId
     * @param status
     */
    @Transactional
    public void updateRechargeOrderStatus(String orderId, String status){
        TRechargeWithdrawOrder tRechargeWithdrawOrder = qryRechargeOrderByOrderId(orderId);
        if(tRechargeWithdrawOrder == null){
            throw new IllegalArgumentException("Recharge order: " + orderId + " not exists");
        }
        if(!tRechargeWithdrawOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_SUCCESS) && !tRechargeWithdrawOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_FAIL)){//不是最终状态
            tRechargeWithdrawOrder.setStatus(status);
            Date date = new Date();
            tRechargeWithdrawOrder.setUpdateTime(date);
            tRechargeWithdrawOrderMapper.updateByPrimaryKey(tRechargeWithdrawOrder);
            if(PayCenterConstant.STATUS_SUCCESS.equalsIgnoreCase(status)){//入金成功，同步增加余额
                AccountBalanceModifyReq req = new AccountBalanceModifyReq();
                req.setBusinessType(tRechargeWithdrawOrder.getBusinessType());
                req.setOrderId(orderId);
                req.setOrderType(Constants.ORDER_TYPE_RECHARGE);
                List<Detail> detailList = new ArrayList<Detail>();
                Detail detail = new Detail();
                detail.setAccountId(tRechargeWithdrawOrder.getAccountId());
                detail.setAmount(tRechargeWithdrawOrder.getAmount());
                detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail.setFeeItem(Constants.FEE_TYPE_RECHARGE);
                detail.setOrgId(tRechargeWithdrawOrder.getOrgId());
                detail.setUserId(tRechargeWithdrawOrder.getUserId());
                detailList.add(detail);
                
                //手续费分配
                TRechargeWithdrawFeeitemExample example = new TRechargeWithdrawFeeitemExample();
                example.createCriteria().andOrderIdEqualTo(orderId).andOrderTypeEqualTo(orderType);
                List<TRechargeWithdrawFeeitem> feeItemList = tRechargeWithdrawFeeitemMapper.selectByExample(example);
                log.info("feeItemList size {}", feeItemList.size());
                if(!CollectionUtils.isEmpty(feeItemList)){
                    for(TRechargeWithdrawFeeitem feeItem : feeItemList){
                        Detail feeItemDetail = new Detail();
                        feeItemDetail.setAccountId(feeItem.getAccountId());
                        feeItemDetail.setAmount(feeItem.getAmount());
                        feeItemDetail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                        feeItemDetail.setFeeItem(Constants.FEE_TYPE_RECHARGE_POUNDAGE);
                        feeItemDetail.setOrgId("");
                        feeItemDetail.setUserId(feeItem.getUserId());
                        detailList.add(feeItemDetail);
                    }
                }
                
                req.setDetailList(detailList);
                accountBusiness.modifyBalance(req);
            }
        }else{
            if(tRechargeWithdrawOrder.getStatus().equalsIgnoreCase(status)){//相同状态重复通知
                log.info("recharge order {} duplicate update", new Object[]{tRechargeWithdrawOrder.getOrderId()});
            }else{//发生了非正常状态变更
                log.warn("Warnning: recharge order {} old status {} new status {}", new Object[]{tRechargeWithdrawOrder.getOrderId(), tRechargeWithdrawOrder.getStatus(), status});
            }
        }
    }
    
    @Transactional
    public TRechargeWithdrawOrder addNewOrder(RechargeH5Request rechargeH5Request){
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.createCriteria().andOrderIdEqualTo(rechargeH5Request.getOrderId()).andOrderTypeEqualTo(orderType);
        List<TRechargeWithdrawOrder> tWineRechargeOrders = tRechargeWithdrawOrderMapper.selectByExample(example);
        if(tWineRechargeOrders!=null&&tWineRechargeOrders.size()>0){
            throw new IllegalArgumentException("order is exits cannot be reRequest");
        }
        
        //入金订单
        TRechargeWithdrawOrder tRechargeWithdrawOrder = new TRechargeWithdrawOrder();
        tRechargeWithdrawOrder.setAccountId(rechargeH5Request.getAccountId());
        tRechargeWithdrawOrder.setAmount(rechargeH5Request.getAmount());
        tRechargeWithdrawOrder.setOrderId(rechargeH5Request.getOrderId());
        tRechargeWithdrawOrder.setUserId(rechargeH5Request.getUserId());
        tRechargeWithdrawOrder.setSignChannel(rechargeH5Request.getChannelCode());
        tRechargeWithdrawOrder.setBusinessType(rechargeH5Request.getBusinessType());
        tRechargeWithdrawOrder.setOrderType(orderType);
        Date date = new Date();
        tRechargeWithdrawOrder.setCreateTime(date);
        tRechargeWithdrawOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_INIT);
        tRechargeWithdrawOrder.setSignAccountId("");
        tRechargeWithdrawOrder.setStatus(PayCenterConstant.STATUS_DOING);
        tRechargeWithdrawOrder.setUpdateTime(date);
        tRechargeWithdrawOrder.setOrgId("");
        tRechargeWithdrawOrderMapper.insert(tRechargeWithdrawOrder);
        
        //手续费列表
        List<FeeItemRequest> feeItemList = rechargeH5Request.getFeeItemList();
        if(!CollectionUtils.isEmpty(feeItemList)){
            for(FeeItemRequest feeItemRequest : feeItemList){
                TRechargeWithdrawFeeitem feeItem = new TRechargeWithdrawFeeitem();
                feeItem.setAccountId(feeItemRequest.getAccountId());
                feeItem.setAmount(feeItemRequest.getFee());
                feeItem.setBusinessType(rechargeH5Request.getBusinessType());
                feeItem.setCreateTime(date);
                feeItem.setOrderId(rechargeH5Request.getOrderId());
                feeItem.setOrderType(orderType);
                feeItem.setUpdateTime(date);
                feeItem.setUserId(feeItemRequest.getUserId());
                tRechargeWithdrawFeeitemMapper.insert(feeItem);
            }
        }
        
        tWineRechargeOrders = tRechargeWithdrawOrderMapper.selectByExample(example);
        return tWineRechargeOrders.get(0);
    }
    
    //调用H5入金接口
    public String callRechargeH5Interface(RechargeH5Request rechargeH5Request){
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getPaycenterBaseUrl() + "/winerecharg/dorechargeh5";
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bankAccount", rechargeH5Request.getBankAccount());
        params.put("amount", rechargeH5Request.getAmount());
        params.put("orderId", rechargeH5Request.getOrderId());
        params.put("businessType", rechargeH5Request.getBusinessType());
        params.put("channelCode", rechargeH5Request.getChannelCode());
        params.put("userId", rechargeH5Request.getUserId());
        params.put("accountId", rechargeH5Request.getAccountId());
        params.put("callbackUrl", rechargeH5Request.getCallbackUrl());
        if(PayCenterConstant.PAY_CODE_ZNPAYH5.equals(rechargeH5Request.getChannelCode())){
            params.put("name", rechargeH5Request.getName());
            params.put("phone", rechargeH5Request.getPhone());
            params.put("certNo", rechargeH5Request.getCertNo());
        }
        String html = "";
        try {
            html = HttpClientUtils.doPost(url, headParams, JSON.toJSONString(params));
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            if(jsonObj.getInteger("rc") != 1){
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }else{
                return jsonObj.getString("data");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
    
    public String queryRechargeStatus(String businessType, String orderId) {
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getPaycenterBaseUrl() + "/recharge/queryresult/" + businessType + "/" + orderId;
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        String html = "";
        try {
            html = HttpClientUtils.doGet(url, headParams, new HashMap<String, String>());
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            int rc = jsonObj.getInteger("rc");
            if(rc == 404){//支付网送无此订单，则返回失败
                return PayCenterConstant.STATUS_FAIL;
            }else if(rc == 1){
                return jsonObj.getJSONObject("data").getString("orderStatus");
            }else{
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
}
