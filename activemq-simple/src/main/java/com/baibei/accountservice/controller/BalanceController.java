package com.baibei.accountservice.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountBalanceSnapshotMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceSnapshot;
import com.baibei.accountservice.model.TAccountBalanceSnapshotExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.Balance;

import lombok.extern.slf4j.Slf4j;

/**
 * 余额接口
 * @author peng
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/account/balance")
@Slf4j
public class BalanceController {
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    TAccountBalanceSnapshotMapper tAccountBalanceSnapshotMapper;
    
    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    /**
     * 可用余额查询
     * @return
     */
    @RequestMapping(value = "/qryBalance/{businessType}/{userId}")
    public BaseResponse<Long> qryBalance(@PathVariable("businessType") String businessType, @PathVariable("userId") String userId){
        try{
            List<String> balanceTypeList = new ArrayList<String>();
            balanceTypeList.add(Constants.BALANCE_TYPE_AVALIABLE);
            List<TAccountBalance> list = accountBusiness.qryRealTimeBalance(qryAccountIdByUserIdAndCheck(userId), balanceTypeList);
            if(!CollectionUtils.isEmpty(list)){
                return RspUtils.success(list.get(0).getAmount());
            }else{
                throw new IllegalArgumentException("余额记录不存在");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    /**
     * 余额查询
     * @return
     */
    @RequestMapping(value = "/qryUserBalance/{businessType}/{userId}")
    public BaseResponse<Balance> qryUserBalance(@PathVariable("businessType") String businessType, @PathVariable("userId") String userId){
        try{
            List<String> balanceTypeList = new ArrayList<String>();
            balanceTypeList.add(Constants.BALANCE_TYPE_AVALIABLE);
            balanceTypeList.add(Constants.BALANCE_TYPE_FREEZON);
            List<TAccountBalance> list = accountBusiness.qryRealTimeBalance(qryAccountIdByUserIdAndCheck(userId), balanceTypeList);
            
            TAccountBalanceSnapshotExample snapshotExample = new TAccountBalanceSnapshotExample();
            snapshotExample.createCriteria().andUserIdEqualTo(userId).andBalanceTypeEqualTo(Constants.BALANCE_TYPE_AVALIABLE);
            snapshotExample.setOrderByClause("id desc limit 1");
            List<TAccountBalanceSnapshot> snapshotList = tAccountBalanceSnapshotMapper.selectByExample(snapshotExample);
            Balance balance = new Balance();
            if(!CollectionUtils.isEmpty(list)){
                for(TAccountBalance tAccountBalance : list){
                    if(Constants.BALANCE_TYPE_AVALIABLE.equalsIgnoreCase(tAccountBalance.getBalanceType())){
                        balance.setCanUseAmount(tAccountBalance.getAmount());
                    }else if(Constants.BALANCE_TYPE_FREEZON.equalsIgnoreCase(tAccountBalance.getBalanceType())){
                        balance.setFreezeAmount(tAccountBalance.getAmount());
                    }
                    if(CollectionUtils.isEmpty(snapshotList)){
                        balance.setCanWithdrawAmount(0L);
                    }else{
                        //最后结算金额
                        long settlementAmount = snapshotList.get(0).getAmount();
                        //最近结算时间
                        Date lastSettleDate = snapshotList.get(0).getCreateTime();
                        
                        //今日累计已申请提现金额
                        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
                        List<String> statusList = new ArrayList<String>();
                        statusList.add(Constants.STATUS_DOING);
                        statusList.add(Constants.STATUS_SUCCESS);
                        example.createCriteria().andUserIdEqualTo(userId).andOrderTypeEqualTo("OUT").andStatusIn(statusList).andCreateTimeGreaterThan(lastSettleDate);
                        List<TRechargeWithdrawOrder> rechargeWithdrawOrderList = tRechargeWithdrawOrderMapper.selectByExample(example);
                        int withdrawSum = 0;
                        if(!CollectionUtils.isEmpty(rechargeWithdrawOrderList)){
                            for(TRechargeWithdrawOrder tRechargeWithdrawOrder : rechargeWithdrawOrderList){
                                withdrawSum += tRechargeWithdrawOrder.getAmount();
                            }
                        }
                        settlementAmount = Math.max(0, settlementAmount - withdrawSum);
                        balance.setCanWithdrawAmount(Math.min(settlementAmount, balance.getCanUseAmount()));
                    }
                }
                return RspUtils.success(balance);
            }else{
                throw new IllegalArgumentException("余额记录不存在");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    //按用户ID查账户ID并检查账户是否存在
    private Long qryAccountIdByUserIdAndCheck(String userId){
        Long buyerAccountId = accountBusiness.qryAccountIdByUserId(userId);
        if(buyerAccountId == null){
            throw new IllegalArgumentException("用户ID=[" + userId + "]的账户记录不存在");
        }
        return buyerAccountId;
    }
}
