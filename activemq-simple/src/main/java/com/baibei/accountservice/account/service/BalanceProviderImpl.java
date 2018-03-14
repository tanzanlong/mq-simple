package com.baibei.accountservice.account.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.account.dto.notify.PaySuccessNotify;
import com.baibei.account.dto.request.DeliveryRequest;
import com.baibei.account.dto.request.FreezeBalanceRequest;
import com.baibei.account.dto.request.LossSettleMatchedRequest;
import com.baibei.account.dto.request.QryTransStatusRequest;
import com.baibei.account.dto.request.SettleLoanFundRequest;
import com.baibei.account.dto.request.SettleLoanInterestRequest;
import com.baibei.account.dto.request.SettleLoanSpotFeeRequest;
import com.baibei.account.dto.request.SettleMatchedRequest;
import com.baibei.account.dto.request.SettleRepaymentRequest;
import com.baibei.account.dto.request.TradeOrderRequest;
import com.baibei.account.dto.request.UnfreezeBalanceRequest;
import com.baibei.account.dto.response.Balance;
import com.baibei.account.dto.response.BalanceAndSignedStatus;
import com.baibei.account.dto.response.QryTransStatusResponse;
import com.baibei.account.provider.BalanceProvider;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.comm.TransStatusEnum;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.rocketmq.RocketMQUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BalanceProviderImpl implements BalanceProvider{

    private final static String MQ_TOPIC_PAY_SUCCESS="DISCREPANCY_MONEY_RESPONSE"; 
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    RocketMQUtils rocketMQUtils;
    
    @Autowired
    private TAccountBalanceMapper tAccountBalanceMapper;
    
    @Override
    public Balance queryBalance(String userId) {
        return accountBusiness.queryBalance(userId);
    }

    @Override
    public List<BalanceAndSignedStatus> queryBalanceList(List<String> userIds,Boolean ignoreZeroAmount) {
    	return accountBusiness.queryBalanceList(userIds,ignoreZeroAmount);
    }

    @Override
    public BalanceAndSignedStatus queryBalanceAndSignedStatus(String userId) {
    	return accountBusiness.queryBalanceAndSignedStatus(userId);
    }
    
    //按用户ID查账户ID并检查账户是否存在
    private Long qryAccountIdByUserIdAndCheck(String userId){
        Long buyerAccountId = accountBusiness.qryAccountIdByUserId(userId);
        if(buyerAccountId == null){
            throw new IllegalArgumentException("用户ID=[" + userId + "]的账户记录不存在");
        }
        return buyerAccountId;
    }

    //资金冻结  记账方法：可用减，冻结加
    @Override
    public void freezeBalance(FreezeBalanceRequest request) {
        //订单类型：资金冻结
        String orderType = Constants.ORDER_TYPE_FREEZE;
        Long accountId = qryAccountIdByUserIdAndCheck(request.getUserId());
        //费用类型：资金冻结
        String feeType = Constants.FEE_TYPE_FREEZE;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getEntrustNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //可用减
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(accountId);
        detail1.setAmount(-1 * request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrgId());
        detail1.setUserId(request.getUserId());
        detailList.add(detail1);
        
        //冻结加
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(accountId);
        detail2.setAmount(request.getAmount());
        detail2.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
        detail2.setFeeItem(feeType);
        detail2.setOrgId(request.getOrgId());
        detail2.setUserId(request.getUserId());
        detailList.add(detail2);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }

    //资金解冻 记账方法：可用加，冻结减
    @Override
    public void unfreezeBalance(UnfreezeBalanceRequest request) {
        //订单类型：资金解冻
        String orderType = Constants.ORDER_TYPE_UNFREEZE;
        String feeType = Constants.FEE_TYPE_UNFREEZE;
        Long accountId = qryAccountIdByUserIdAndCheck(request.getUserId());
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
        
        //冻结减
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(accountId);
        detail2.setAmount(-1 * request.getAmount());
        detail2.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
        detail2.setFeeItem(feeType);
        detail2.setOrgId(request.getOrgId());
        detail2.setUserId(request.getUserId());
        detailList.add(detail2);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }

    //撮合成交 记账方法：买方冻结减货款，卖方可用加货款，买卖双方付手续费，手续费分成参与各方分手续费
    @Override
    public void settleMatched(SettleMatchedRequest request) {
        try{
            log.info(" settleMatched paramp  settleMatched:{}",JSON.toJSONString(request));
            //订单类型：撮合成交
            String orderType = Constants.ORDER_TYPE_TRADE;
            Long buyerAccountId = qryAccountIdByUserIdAndCheck(request.getBuyerId());
            Long sellerAccountId = qryAccountIdByUserIdAndCheck(request.getSellerId());
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
            req.setOrderId(request.getTradeNum());
            req.setOrderType(orderType);
            List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
            //买方
            AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
            detail1.setAccountId(buyerAccountId);
            detail1.setAmount(-1 * request.getAmount());
            detail1.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
            detail1.setFeeItem(Constants.FEE_TYPE_BUYTRADE);
            detail1.setOrgId(request.getBueryOrgId());
            detail1.setUserId(request.getBuyerId());
            detailList.add(detail1);
            
            //卖方
            AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
            detail2.setAccountId(sellerAccountId);
            detail2.setAmount(request.getAmount());
            detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail2.setFeeItem(Constants.FEE_TYPE_SELLTRADE);
            detail2.setOrgId(request.getSellerOrgId());
            detail2.setUserId(request.getSellerId());
            detailList.add(detail2);
            
            //手续费分配列表
            List<SettleMatchedRequest.FeeItem> feeItemList = request.getFeeList();
            if(CollectionUtils.isNotEmpty(feeItemList)){
                //零和检查
                long sum = 0;
                for(SettleMatchedRequest.FeeItem feeItem : feeItemList){
                    AccountBalanceModifyReq.Detail detail = new AccountBalanceModifyReq.Detail();
                    Long accountId = qryAccountIdByUserIdAndCheck(feeItem.getUserId());
                    detail.setAccountId(accountId);
                    detail.setAmount(feeItem.getFee());
                    sum += feeItem.getFee();
                    if(accountId - buyerAccountId == 0){//买方
                        detail.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
                    }else if(accountId - sellerAccountId == 0){//卖方
                        detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    }else{//其它方
                        //detail.setBalanceType(Constants.BALANCE_TYPE_POUNDAGE_TRADE);
                        detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    }
                    if(feeItem.getType() == 0){//买
                        detail.setFeeItem(Constants.FEE_TYPE_BUYTRADE_POUNDAGE);
                    }else{//卖
                        detail.setFeeItem(Constants.FEE_TYPE_SELLTRADE_POUNDAGE);
                    }
                    detail.setOrgId(feeItem.getOrgId());
                    detail.setUserId(feeItem.getUserId());
                    detailList.add(detail);
                }
                if(sum != 0){
                    throw new IllegalArgumentException("手续列表费用总和必须为0");
                }
            }
            req.setDetailList(detailList);
            accountBusiness.modifyBalance(req);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //融资 记账方法：借入方可用增加，出借方可用减少，借入方可用扣取续费，融资手续费各分成方手续费收入增加
    @Override
    public void settleLoanFund(SettleLoanFundRequest request) {
        try{
            String orderType = Constants.ORDER_TYPE_LOAN;
            Long borrowAccountId = qryAccountIdByUserIdAndCheck(request.getBorrowId());
            Long lenderAccountId = qryAccountIdByUserIdAndCheck(request.getLenderId());
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
            req.setOrderId(request.getOrderNum());
            req.setOrderType(orderType);
            List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
            //借入方
            AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
            detail1.setAccountId(borrowAccountId);
            detail1.setAmount(request.getAmount());
            detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail1.setFeeItem(Constants.FEE_TYPE_LOAN);
            detail1.setOrgId(request.getBorrowOrgId());
            detail1.setUserId(request.getBorrowId());
            detailList.add(detail1);
            
            //借入方 余额类型为融资的账户变更，不记账
            AccountBalanceModifyReq.Detail detail1Sumary = new AccountBalanceModifyReq.Detail();
            detail1Sumary.setAccountId(borrowAccountId);
            detail1Sumary.setAmount(request.getAmount());
            detail1Sumary.setBalanceType(Constants.BALANCE_TYPE_LOAN);
            detail1Sumary.setFeeItem(Constants.FEE_TYPE_LOAN);
            detail1Sumary.setOrgId(request.getBorrowOrgId());
            detail1Sumary.setUserId(request.getBorrowId());
            detailList.add(detail1Sumary);
            
            //出借方
            AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
            detail2.setAccountId(lenderAccountId);
            detail2.setAmount(-1 * request.getAmount());
            detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail2.setFeeItem(Constants.FEE_TYPE_LOAN);
            detail2.setOrgId(request.getLenderOrgId());
            detail2.setUserId(request.getLenderId());
            detailList.add(detail2);
            
            //手续费分配列表
            List<SettleLoanFundRequest.FeeItem> feeItemList = request.getFeeList();
            if(CollectionUtils.isNotEmpty(feeItemList)){
                //零和检查
                long sum = 0;
                for(SettleLoanFundRequest.FeeItem feeItem : feeItemList){
                    AccountBalanceModifyReq.Detail detail = new AccountBalanceModifyReq.Detail();
                    Long accountId = qryAccountIdByUserIdAndCheck(feeItem.getUserId());
                    detail.setAccountId(accountId);
                    detail.setAmount(feeItem.getFee());
                    sum += feeItem.getFee();
                    if(accountId - borrowAccountId == 0){//借入方
                        detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    }else{
                        //detail.setBalanceType(Constants.BALANCE_TYPE_POUNDAGE_FINANCING);
                        detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    }
                    detail.setFeeItem(Constants.FEE_TYPE_LOAN_POUNDAGE);
                    detail.setOrgId(feeItem.getOrgId());
                    detail.setUserId(feeItem.getUserId());
                    detailList.add(detail);
                }
                if(sum != 0){
                    throw new IllegalArgumentException("手续列表费用总和必须为0");
                }
            }
            req.setDetailList(detailList);
            accountBusiness.modifyBalance(req);
        }catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //融货手续费分成 记账方法：借入方可用减少，融货手续费各分成参与方融货手续费收入增加
    @Override
    public void settleLoanSpotFee(SettleLoanSpotFeeRequest request) {
        //订单类型：融货
        String orderType = Constants.ORDER_TYPE_MARGIN;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getOrderNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //手续费分配列表
        List<SettleLoanSpotFeeRequest.FeeItem> feeItemList = request.getFeeList();
        if(CollectionUtils.isNotEmpty(feeItemList)){
            //零和检查
            long sum = 0;
            for(SettleLoanSpotFeeRequest.FeeItem feeItem : feeItemList){
                AccountBalanceModifyReq.Detail detail = new AccountBalanceModifyReq.Detail();
                Long accountId = qryAccountIdByUserIdAndCheck(feeItem.getUserId());
                detail.setAccountId(accountId);
                detail.setAmount(feeItem.getFee());
                sum += feeItem.getFee();
                if(request.getBorrowId().equals(feeItem.getUserId())){//借入方
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                }else{
                   //detail.setBalanceType(Constants.BALANCE_TYPE_POUNDAGE_FINANCING);
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                }
                detail.setFeeItem(Constants.FEE_TYPE_MARGIN_POUNDAGE);
                detail.setOrgId(feeItem.getOrgId());
                detail.setUserId(feeItem.getUserId());
                detailList.add(detail);
            }
            if(sum != 0){
                throw new IllegalArgumentException("手续列表费用总和必须为0");
            }
        }
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }

    //还款 记账方法：借入方可用减少，出借方可用增加
    @Override
    public void settleRepayment(SettleRepaymentRequest request) {
        //订单类型：还款
        String orderType = Constants.ORDER_TYPE_REPAYMENT;
        Long borrowAccountId = qryAccountIdByUserIdAndCheck(request.getBorrowId());
        Long lenderAccountId = qryAccountIdByUserIdAndCheck(request.getLenderId());
        String feeType = Constants.FEE_TYPE_REPAYMENT;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getOrderNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        if(request.getAdvancedAmount() > 0){//发生了垫付
            //买方借未付款
            AccountBalanceModifyReq.Detail detail0 = new AccountBalanceModifyReq.Detail();
            detail0.setAccountId(borrowAccountId);
            detail0.setAmount(request.getAdvancedAmount());
            //可用
            detail0.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            //强制还款垫付
            detail0.setFeeItem(Constants.FEE_TYPE_LOSS_REPAYMENT);
            detail0.setOrgId(request.getBorrowOrgId());
            detail0.setUserId(request.getBorrowId());
            detailList.add(detail0);
        }
        
        //借入方
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(borrowAccountId);
        detail1.setAmount(-1 * request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getBorrowOrgId());
        detail1.setUserId(request.getBorrowId());
        detailList.add(detail1);
        
        //借入方 余额类型为融资的账户变更，不记账
        AccountBalanceModifyReq.Detail detail1Sumary = new AccountBalanceModifyReq.Detail();
        detail1Sumary.setAccountId(borrowAccountId);
        detail1Sumary.setAmount(-1 * request.getAmount());
        detail1Sumary.setBalanceType(Constants.BALANCE_TYPE_LOAN);
        detail1Sumary.setFeeItem(Constants.FEE_TYPE_LOAN);
        detail1Sumary.setOrgId(request.getBorrowOrgId());
        detail1Sumary.setUserId(request.getBorrowId());
        detailList.add(detail1Sumary);
        
        //出借方
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(lenderAccountId);
        detail2.setAmount(request.getAmount());
        detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail2.setFeeItem(feeType);
        detail2.setOrgId(request.getLenderOrgId());
        detail2.setUserId(request.getLenderId());
        detailList.add(detail2);
        
        if(request.getAdvancedAmount() > 0){//只有垫付款大于0时才处理，如果未发生垫付，则垫付款为0
            TAccount clearCenterAccount = accountBusiness.qryClearCenterAccount();
            Long advancedAccountId = clearCenterAccount.getAccountId();
            String advancedUserId = clearCenterAccount.getUserId();
            //垫付方
            AccountBalanceModifyReq.Detail detail3 = new AccountBalanceModifyReq.Detail();
            detail3.setAccountId(advancedAccountId);
            detail3.setAmount(-1 * request.getAdvancedAmount());
            detail3.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail3.setFeeItem(Constants.FEE_TYPE_LOSSBUYTRADE_ADVANCED);
            detail3.setOrgId("");
            detail3.setUserId(advancedUserId);
            detailList.add(detail3);
            
            //未付款项 余额类型为未付款的账户变更，不记账
            AccountBalanceModifyReq.Detail detail4 = new AccountBalanceModifyReq.Detail();
            detail4.setAccountId(borrowAccountId);
            detail4.setAmount(request.getAdvancedAmount());
            detail4.setBalanceType(Constants.BALANCE_TYPE_UNPAY);
            detail4.setFeeItem(Constants.FEE_TYPE_REPAYMENT);
            detail4.setOrgId(request.getBorrowOrgId());
            detail4.setUserId(request.getBorrowId());
            detailList.add(detail4);
        }
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }

    //扣息，记账方法：借入方可用扣息减少，融资机构利息收入增加
    @Override
    public void settleLoanInterest(SettleLoanInterestRequest request) {
        //订单类型：扣息
        String orderType = Constants.ORDER_TYPE_LOANINTEREST;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getOrderNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //利息分配列表
        List<SettleLoanInterestRequest.FeeItem> feeItemList = request.getInterestList();
        if(CollectionUtils.isNotEmpty(feeItemList)){
            //零和检查
            long sum = 0;
            for(SettleLoanInterestRequest.FeeItem feeItem : feeItemList){
                AccountBalanceModifyReq.Detail detail = new AccountBalanceModifyReq.Detail();
                Long accountId = qryAccountIdByUserIdAndCheck(feeItem.getUserId());
                detail.setAccountId(accountId);
                detail.setAmount(feeItem.getFee());
                sum += feeItem.getFee();
                if(request.getBorrowId().equals(detail.getUserId())){//借入方
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                }else{
                    //detail.setBalanceType(Constants.BALANCE_TYPE_FINANCING_INTERESTS);
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                }
                if("fund".equalsIgnoreCase(request.getType())){//融资
                    detail.setFeeItem(Constants.FEE_TYPE_LOANINTEREST);
                }else{//融货
                    detail.setFeeItem(Constants.FEE_TYPE_MARGININTEREST);
                }
                detail.setOrgId(feeItem.getOrgId());
                detail.setUserId(feeItem.getUserId());
                detailList.add(detail);
            }
            if(sum != 0){
                throw new IllegalArgumentException("利息分成列表费用总和必须为0");
            }
        }
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
    }

    @Override
    public void delivery(DeliveryRequest request) {
        //订单类型：交收
        String orderType = Constants.ORDER_TYPE_DELIVERY;
        Long ordererAccountId = qryAccountIdByUserIdAndCheck(request.getOrdererId());
        Long invoiderAccountId = qryAccountIdByUserIdAndCheck(request.getInvoicerId());
        String feeType = Constants.FEE_TYPE_DELIVERY_PREMIUM;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getOrderNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //订货方
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(ordererAccountId);
        detail1.setAmount(request.getOrdererIncomeAmount());
        if(request.getOrdererIncomeAmount() < 0){//订货方付款，从冻结中付
            detail1.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
        }else{//收款，直接加到可用
            detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        }
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrdererOrgId());
        detail1.setUserId(request.getOrdererId());
        detailList.add(detail1);
        
        //发货方
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(invoiderAccountId);
        detail2.setAmount(request.getInvoicerIncomeAmount());
        if(request.getInvoicerIncomeAmount() < 0){//发货方付款，从冻结中付
            detail2.setBalanceType(Constants.BALANCE_TYPE_FREEZON);  
        }else{//收款，直接加到可用
            detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE); 
        }
        detail2.setFeeItem(feeType);
        detail2.setOrgId(request.getInvoicerOrgId());
        detail2.setUserId(request.getInvoicerId());
        detailList.add(detail2);
        req.setDetailList(detailList);
        
        //零和检查
        if(request.getOrdererIncomeAmount() + request.getInvoicerIncomeAmount() != 0){
            throw new IllegalArgumentException("金额总和必须为0");
        }
        accountBusiness.modifyBalance(req);
        
        //发MQ
        try{
            sendDeliverySuccessMq(detail1.getUserId(), detail1.getAmount());
            sendDeliverySuccessMq(detail2.getUserId(), detail2.getAmount());
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
    
    public void sendDeliverySuccessMq(String userId, Long amount){
        TAccountBalanceExample tAccountBalanceExample=new TAccountBalanceExample();
        tAccountBalanceExample.createCriteria().andUserIdEqualTo(userId).andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_USEABLE);
        List<TAccountBalance> tAccountBalances= tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
        if(tAccountBalances==null||tAccountBalances.size()<=0){
            return ;
        }
        TAccountBalance tAccountBalance=tAccountBalances.get(0);
        PaySuccessNotify paySuccessNotify = new PaySuccessNotify();
        paySuccessNotify.setChangeMoney(amount);
        paySuccessNotify.setTotalMoney(tAccountBalance.getAmount());
        paySuccessNotify.setType("DELIVERY");
        paySuccessNotify.setUserID(userId);
        try {
            rocketMQUtils.send(MQ_TOPIC_PAY_SUCCESS, JSON.toJSONString(paySuccessNotify));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void tradeOrder(TradeOrderRequest request) {
        //订单类型：贸易
        String orderType = Constants.ORDER_TYPE_TRADEORDER;
        Long ordererAccountId = qryAccountIdByUserIdAndCheck(request.getOrdererId());
        Long invoiderAccountId = qryAccountIdByUserIdAndCheck(request.getInvoicerId());
        String feeType = Constants.FEE_TYPE_TRADEORDER;
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getOrderNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        //订货方
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(ordererAccountId);
        detail1.setAmount(request.getAmount());
        detail1.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
        detail1.setFeeItem(feeType);
        detail1.setOrgId(request.getOrdererOrgId());
        detail1.setUserId(request.getOrdererId());
        detailList.add(detail1);
        
        //发货方
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(invoiderAccountId);
        detail2.setAmount(-1*request.getAmount());
        detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail2.setFeeItem(feeType);
        detail2.setOrgId(request.getInvoicerOrgId());
        detail2.setUserId(request.getInvoicerId());
        detailList.add(detail2);
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
        
        //发MQ
        try{
            sendDeliverySuccessMq(detail1.getUserId(), detail1.getAmount());
            sendDeliverySuccessMq(detail2.getUserId(), detail2.getAmount());
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }

    //强制买货交易
    @Override
    public void lossSettleMatched(LossSettleMatchedRequest request) {
        log.info(" settleMatched paramp  lossSettleMatched:{}",JSON.toJSONString(request));
        //订单类型：撮合成交
        String orderType = Constants.ORDER_TYPE_TRADE;
        Long buyerAccountId = qryAccountIdByUserIdAndCheck(request.getBuyerId());
        Long sellerAccountId = qryAccountIdByUserIdAndCheck(request.getSellerId());
        Long advancedAccountId = qryAccountIdByUserIdAndCheck(request.getAdvancedUserId());
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(Constants.BUSINESS_TYPE_DEFAULT);
        req.setOrderId(request.getTradeNum());
        req.setOrderType(orderType);
        List<AccountBalanceModifyReq.Detail> detailList = new ArrayList<AccountBalanceModifyReq.Detail>();
        
        if(request.getAdvancedAmount() > 0){//发生了垫付
            //买方借未付款
            AccountBalanceModifyReq.Detail detail0 = new AccountBalanceModifyReq.Detail();
            detail0.setAccountId(buyerAccountId);
            detail0.setAmount(request.getAdvancedAmount());
            //可用
            detail0.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            //强制交易垫付
            detail0.setFeeItem(Constants.FEE_TYPE_LOSSBUYTRADE_ADVANCED);
            detail0.setOrgId(request.getBueryOrgId());
            detail0.setUserId(request.getBuyerId());
            detailList.add(detail0);
        }
        
        //买方
        AccountBalanceModifyReq.Detail detail1 = new AccountBalanceModifyReq.Detail();
        detail1.setAccountId(buyerAccountId);
        detail1.setAmount(-1 * request.getAmount());
        //可用
        detail1.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        //强制交易
        detail1.setFeeItem(Constants.FEE_TYPE_LOSSBUYTRADE);
        detail1.setOrgId(request.getBueryOrgId());
        detail1.setUserId(request.getBuyerId());
        detailList.add(detail1);
        
        //卖方
        AccountBalanceModifyReq.Detail detail2 = new AccountBalanceModifyReq.Detail();
        detail2.setAccountId(sellerAccountId);
        detail2.setAmount(request.getAmount());
        detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
        detail2.setFeeItem(Constants.FEE_TYPE_SELLTRADE);
        detail2.setOrgId(request.getSellerOrgId());
        detail2.setUserId(request.getSellerId());
        detailList.add(detail2);
        
        if(request.getAdvancedAmount() > 0){//只有垫付款大于0时才处理，如果未发生垫付，则垫付款为0
            //垫付方
            AccountBalanceModifyReq.Detail detail3 = new AccountBalanceModifyReq.Detail();
            detail3.setAccountId(advancedAccountId);
            detail3.setAmount(-1 * request.getAdvancedAmount());
            detail3.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail3.setFeeItem(Constants.FEE_TYPE_LOSSBUYTRADE_ADVANCED);
            detail3.setOrgId(request.getAdvancedOrgId());
            detail3.setUserId(request.getAdvancedUserId());
            detailList.add(detail3);
            
            //未付款项 余额类型为未付款的账户变更，不记账
            AccountBalanceModifyReq.Detail detail4 = new AccountBalanceModifyReq.Detail();
            detail4.setAccountId(buyerAccountId);
            detail4.setAmount(request.getAdvancedAmount());
            detail4.setBalanceType(Constants.BALANCE_TYPE_UNPAY);
            detail4.setFeeItem(Constants.FEE_TYPE_LOSSBUYTRADE);
            detail4.setOrgId(request.getBueryOrgId());
            detail4.setUserId(request.getBuyerId());
            detailList.add(detail4);
        }
        
        //手续费分配列表
        List<LossSettleMatchedRequest.FeeItem> feeItemList = request.getFeeList();
        long poundageSum = 0;
        if(CollectionUtils.isNotEmpty(feeItemList)){
            //零和检查
            long sum = 0;
            for(LossSettleMatchedRequest.FeeItem feeItem : feeItemList){
                AccountBalanceModifyReq.Detail detail = new AccountBalanceModifyReq.Detail();
                Long accountId = qryAccountIdByUserIdAndCheck(feeItem.getUserId());
                detail.setAccountId(accountId);
                detail.setAmount(feeItem.getFee());
                sum += feeItem.getFee();
                if(accountId - buyerAccountId == 0){//买方
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    poundageSum += -1 * feeItem.getFee();
                }else if(accountId - sellerAccountId == 0){//卖方
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    poundageSum += -1 * feeItem.getFee();
                }else{//其它方
                    //detail.setBalanceType(Constants.BALANCE_TYPE_POUNDAGE_TRADE);
                    detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                }
                if(feeItem.getType() == 0){//买
                    detail.setFeeItem(Constants.FEE_TYPE_BUYTRADE_POUNDAGE);
                }else{//卖
                    detail.setFeeItem(Constants.FEE_TYPE_SELLTRADE_POUNDAGE);
                }
                detail.setOrgId(feeItem.getOrgId());
                detail.setUserId(feeItem.getUserId());
                detailList.add(detail);
            }
            if(sum != 0){
                throw new IllegalArgumentException("手续列表费用总和必须为0");
            }
        }
        if((poundageSum + request.getAmount()) != (request.getBuyerPayAmount() + request.getAdvancedAmount())){
           //throw new IllegalArgumentException("买方应付款+垫付款必须等于货款+手续费");
        }
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
        
    }

    //查询交易状态
    @Override
    public QryTransStatusResponse qryTransStatus(QryTransStatusRequest request) {
        if(request.getTransNum()==null){
            throw new IllegalArgumentException("parameter transNum can not be blank");
        }
        
        QryTransStatusResponse response = new QryTransStatusResponse();
        response.setTransNum(request.getTransNum());
        if(!accountBusiness.checkTransIsNotExists(Constants.ORDER_TYPE_FREEZE, request.getTransNum())){//冻结单存在
            if(accountBusiness.checkTransIsNotExists(Constants.ORDER_TYPE_UNFREEZE, request.getTransNum())){//对应的解冻单不存在
                response.setTransStatus(TransStatusEnum.SUCCESS.getCode());
            }else{
                response.setTransStatus(TransStatusEnum.CANCELED.getCode());
            }
        }else{
            response.setTransStatus(TransStatusEnum.NOTEXISTS.getCode());
        }
        return response;
    }

    @Override
    public void rollbackFreezeBalance(FreezeBalanceRequest request) {
        //查询原冻结单是否成功
        QryTransStatusRequest qryTransStatusRequest = new QryTransStatusRequest();
        qryTransStatusRequest.setTransNum(request.getEntrustNum());
        QryTransStatusResponse qryTransStatusResponse = this.qryTransStatus(qryTransStatusRequest);
        if(TransStatusEnum.NOTEXISTS.getCode().equalsIgnoreCase(qryTransStatusResponse.getTransStatus())){//原单不存在，则无需回退
            return;
        }else if(TransStatusEnum.CANCELED.getCode().equalsIgnoreCase(qryTransStatusResponse.getTransStatus())){//原单已回退，则无需再回退
            return;
        }
        
        //调用解冻接口
        UnfreezeBalanceRequest unfreezeBalanceRequest = new UnfreezeBalanceRequest();
        unfreezeBalanceRequest.setAmount(request.getAmount());
        unfreezeBalanceRequest.setEntrustNum(request.getEntrustNum());
        unfreezeBalanceRequest.setOrgId(request.getOrgId());
        unfreezeBalanceRequest.setUserId(request.getUserId());
        this.unfreezeBalance(unfreezeBalanceRequest);
    }
}
