package com.baibei.accountservice.paycenter.bussiness.ch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq;
import com.baibei.accountservice.paycenter.dto.request.RechargeBalanceRequest;
import com.baibei.accountservice.paycenter.dto.request.WithdrawBalanceRequest;
@Service
public class PayBalanceBusiness {
    @Autowired
    AccountBusiness accountBusiness;
    //资金  记账方法：可用加
    public void rechargeBalance(RechargeBalanceRequest request) {
        //订单类型：充值
        String orderType = Constants.ORDER_TYPE_RECHARGE;
        Long accountId = qryAccountIdByUserIdAndCheck(request.getUserId());
        //费用类型：充值
        String feeType = Constants.FEE_TYPE_RECHARGE;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getEntrustNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //可用加
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(accountId);
        detail1.setAmount(request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrgId());
        detail1.setUserId(request.getUserId());
        detailList.add(detail1);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }
    
    
    //资金 记账方法：可用减
    public void subAvalaibleInWthdraw(WithdrawBalanceRequest request) {
        //订单类型：充值
        String orderType = Constants.ORDER_TYPE_WITHDRAW;
        Long accountId = qryAccountIdByUserIdAndCheck(request.getUserId());
        //费用类型：充值
        String feeType = Constants.FEE_TYPE_WITHDRAW;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getEntrustNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //可用加
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(accountId);
        detail1.setAmount(-1*request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrgId());
        detail1.setUserId(request.getUserId());
        detailList.add(detail1);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }
    
    
    public void addAvalaibleBalanceInWthdraw(WithdrawBalanceRequest request) {
        //订单类型：充值
        String orderType = Constants.ORDER_TYPE_WITHDRAW;
        Long accountId = qryAccountIdByUserIdAndCheck(request.getUserId());
        //费用类型：充值
        String feeType = Constants.FEE_TYPE_WITHDRAW;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getEntrustNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //可用加
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(accountId);
        detail1.setAmount(-1*request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrgId());
        detail1.setUserId(request.getUserId());
        detailList.add(detail1);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }
    
    
    
    //按用户ID查账户ID并检查账户是否存在
    private Long qryAccountIdByUserIdAndCheck(String userId){
        Long rechargeAccountId = accountBusiness.qryAccountIdByUserId(userId);
        if(rechargeAccountId == null){
            throw new IllegalArgumentException("用户ID=[" + userId + "]的账户记录不存在");
        }
        return rechargeAccountId;
    }
}
