package com.baibei.accountservice.paycenter.service;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.account.dto.request.PayLimitSetRequest;
import com.baibei.account.dto.request.QueryTransferRecordsRequest;
import com.baibei.account.dto.request.TransferRequest;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.dto.response.TransferRecord;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TPayLimitMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TPayLimit;
import com.baibei.accountservice.model.TPayLimitExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.paycenter.bussiness.ch.RechargeBussiness;
import com.baibei.accountservice.paycenter.bussiness.ch.RechargeWithdrawBusiness;
import com.baibei.accountservice.paycenter.bussiness.ch.WithdrawBussiness;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.dto.response.RechargeResponse;
import com.baibei.accountservice.paycenter.dto.response.WithdrawResponse;
import com.baibei.accountservice.paycenter.exception.PasswordException;
import com.baibei.accountservice.paycenter.exception.PayException;
import com.baibei.accountservice.paycenter.provider.TransferringProvider;
@Component
@Slf4j
public class TransferringProviderImpl implements TransferringProvider {
    
    @Autowired
    private RechargeBussiness rechargeBussiness;
    
    @Autowired
    private WithdrawBussiness withdrawBussiness;
    
    @Autowired
    private RechargeWithdrawBusiness rechargeWithdrawBusiness;
    
    @Autowired
    private TPayLimitMapper tPayLimitMapper;
    
    @Autowired
    private TAccountMapper tAccountMapper;

    @Override
    public Long queryWithdrawableBalance(String userId) {
        return withdrawBussiness.queryCanWithdrawAmount(userId);
    }

    @Override
    public RechargeResponse transferIn(TransferRequest transferRequest) throws PayException, PasswordException {
        RechargeResponse rechargeResponse= rechargeBussiness.rechargeRequest(transferRequest);
      return  rechargeResponse;
    }

    @Override
    public WithdrawResponse transferOut(TransferRequest transferRequest) throws PayException, PasswordException {
      return  withdrawBussiness.withdrawRequest(transferRequest);

    }

    @Override
    public String queryTransferInResult(String orderId) {
        String status = PayCenterConstant.STATUS_DOING;
        TRechargeWithdrawOrder tRechargeOrder = rechargeBussiness.qryTRechargeOrderByOrderId(orderId);
        if (tRechargeOrder != null) {
            status = tRechargeOrder.getStatus();
        }
        return status;
    }

    @Override
    public String queryTransferOutResult(String orderId) {
        String status = PayCenterConstant.STATUS_DOING;
        TRechargeWithdrawOrder tWithdrawOrder = withdrawBussiness.qryTWithdrawOrderByOrderId(orderId);
        if (tWithdrawOrder != null) {
            status = tWithdrawOrder.getStatus();
        }
        return status;
    }

    @Override
    public PageResponse<List<TransferRecord>> queryTransferRecords(
            QueryTransferRecordsRequest request) {
        log.info(" queryTransferRecords :{}", JSON.toJSONString(request));
        /*
         * if("IN".equals(type)){ return rechargeBussiness.queryRechargeLogByUserId(request); }else{
         * return withdrawBussiness.queryWithdrawLogByUserId(request); }
         */
        return rechargeWithdrawBusiness.queryRechargeLogByUserId(request);
    }

    @Override
    public boolean payLimitSet(PayLimitSetRequest payLimitSetRequest) {
        log.info(" payLimitSet param:{} ",JSON.toJSONString(payLimitSetRequest));
        String userId=payLimitSetRequest.getUserId();
        String withdrawOrRecharge=payLimitSetRequest.getWithdrawOrRecharge();
        Boolean isCanPay=payLimitSetRequest.getIsCanPay();
        if(isCanPay==null){
            throw new IllegalArgumentException("请先指定限制方向");
        }
        if(StringUtils.isBlank(userId)){
            throw new IllegalArgumentException("请先注册");
        }
        if(!"IN".equalsIgnoreCase(withdrawOrRecharge)&&!"OUT".equals(withdrawOrRecharge)){
            throw new IllegalArgumentException("请先指定限制出金还是入金");
        }
        TAccountExample tAccountExample=new TAccountExample();
        tAccountExample.createCriteria().andUserIdEqualTo(payLimitSetRequest.getUserId()).andIsDelEqualTo(0);
        List<TAccount> tAccounts=tAccountMapper.selectByExample(tAccountExample);
        if(tAccounts==null||tAccounts.size()<=0){
            return false;
        }
        
        TPayLimitExample tPayLimitExample=new TPayLimitExample();
        tPayLimitExample.createCriteria().andUserIdEqualTo(userId);
        List<TPayLimit> tPayLimits= tPayLimitMapper.selectByExample(tPayLimitExample);
        if(tPayLimits==null||tPayLimits.size()<=0){
            TPayLimit tPayLimit=new TPayLimit();
            Date now=new Date();
            tPayLimit.setCanNotRecharge(0);
            tPayLimit.setCanNotWithdraw(0);
            tPayLimit.setCreateTime(now);
            tPayLimit.setUpdateTime(now);
            tPayLimit.setUserId(userId);
            tPayLimit.setAccountId(tAccounts.get(0).getAccountId());
            tPayLimitMapper.insert(tPayLimit);
            tPayLimits= tPayLimitMapper.selectByExample(tPayLimitExample);
        }
        TPayLimit tPayLimit=tPayLimits.get(0);
        
        if("IN".equalsIgnoreCase(withdrawOrRecharge)){
            if(isCanPay==false){
                tPayLimit.setCanNotRecharge(1);
            }else{
                tPayLimit.setCanNotRecharge(0);
            }
        }else if("OUT".equalsIgnoreCase(withdrawOrRecharge)){
            if(isCanPay==false){
                tPayLimit.setCanNotWithdraw(1);;
            }else{
                tPayLimit.setCanNotWithdraw(0);
            }  
        }
        tPayLimitMapper.updateByPrimaryKeySelective(tPayLimit);
        return true;
    }

    
    
}
