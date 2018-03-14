package com.baibei.accountservice.paycenter.bussiness;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAbnormalOrderLogMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TAbnormalOrderLog;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.dto.NotifyAbonormalOrderRequest;

/**
 * 异常出入金订单
 * @author peng
 *
 */
@Service
public class PayCenterAbnormalOrderBusiness {
    
    @Autowired
    TAbnormalOrderLogMapper TAbnormalOrderLogMapper;
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;

    /**
     * 异常出入金异步回调通知
     * 
     * @param notifyAbonormalOrderRequest
     */
    public String notifyAbnormalOrder(NotifyAbonormalOrderRequest notifyAbonormalOrderRequest) {
        this.checkParam(notifyAbonormalOrderRequest);
        this.saveAbnormalOrder(notifyAbonormalOrderRequest);
        return "OK";
    }

    /**
     * 参数校验
     * 
     * @param notifyAbonormalOrderRequest
     */
    private void checkParam(NotifyAbonormalOrderRequest notifyAbonormalOrderRequest) {
        if (notifyAbonormalOrderRequest.getChannel() == null) {
            throw new IllegalArgumentException("parameter Channel can not be blank");
        }
        if (notifyAbonormalOrderRequest.getChannelAmount() == null) {
            throw new IllegalArgumentException("parameter ChannelAmount can not be blank");
        }
        if (notifyAbonormalOrderRequest.getChannelStatus() == null) {
            throw new IllegalArgumentException("parameter ChannelStatus can not be blank");
        }
        if (notifyAbonormalOrderRequest.getOrderId() == null) {
            throw new IllegalArgumentException("parameter OrderId can not be blank");
        }
        if (notifyAbonormalOrderRequest.getOrderType() == null) {
            throw new IllegalArgumentException("parameter OrderType can not be blank");
        }
        if (notifyAbonormalOrderRequest.getOrgAmount() == null) {
            throw new IllegalArgumentException("parameter OrgAmount can not be blank");
        }
        if (notifyAbonormalOrderRequest.getOrgStatus() == null) {
            throw new IllegalArgumentException("parameter OrgStatus can not be blank");
        }
        if (notifyAbonormalOrderRequest.getSign() == null) {
            throw new IllegalArgumentException("parameter Sign can not be blank");
        }
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void saveAbnormalOrder(NotifyAbonormalOrderRequest notifyAbonormalOrderRequest){
        Date now=new Date();
        TAbnormalOrderLog tAbnormalOrderLog=new TAbnormalOrderLog();
        tAbnormalOrderLog.setChannel(notifyAbonormalOrderRequest.getChannel());
        tAbnormalOrderLog.setChannelAmount(notifyAbonormalOrderRequest.getChannelAmount());
        tAbnormalOrderLog.setChannelStatus(notifyAbonormalOrderRequest.getChannelStatus());
        tAbnormalOrderLog.setCreateTime(now);
        tAbnormalOrderLog.setOrderId(notifyAbonormalOrderRequest.getOrderId());
        tAbnormalOrderLog.setOrderType(notifyAbonormalOrderRequest.getOrderType());
        tAbnormalOrderLog.setOrgAmount(notifyAbonormalOrderRequest.getOrgAmount());
        tAbnormalOrderLog.setOrgStatus(notifyAbonormalOrderRequest.getOrgStatus());
        tAbnormalOrderLog.setUpdateTime(now);
        TAbnormalOrderLogMapper.insert(tAbnormalOrderLog);
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void handleAbnormalOrder(TAbnormalOrderLog tAbnormalOrderLog){
        //长款，则退回用户余额
        //短款，如果用户余额足够，则扣除用户余额，否则转人工处理
        //简单且安全起见，同一订单出现多次调整或处理出现异常，则转手工处理
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        if("IN".equalsIgnoreCase(tAbnormalOrderLog.getOrderType())){//入金
            example.createCriteria().andOrderIdEqualTo(tAbnormalOrderLog.getOrderId()).andOrderTypeEqualTo("IN");
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            
            if(Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus()) && Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus())){//入金原本失败，后面调账为成功
                
            }else if(Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus()) && Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus())){//入金原本成功，后面调账为失败
                
            }
        }else if("OUT".equalsIgnoreCase(tAbnormalOrderLog.getOrderType())){//出金
            example.createCriteria().andOrderIdEqualTo(tAbnormalOrderLog.getOrderId()).andOrderTypeEqualTo("OUT");
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            if(Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus()) && Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus())){//出金原本成功，后面调账为失败
                
            }else if(Constants.STATUS_FAIL.equalsIgnoreCase(tAbnormalOrderLog.getOrgStatus()) && Constants.STATUS_SUCCESS.equalsIgnoreCase(tAbnormalOrderLog.getChannelStatus())){//出金原本失败，后面调账为成功
                
            }
        }else{
            
        }
    }
}
