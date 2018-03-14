package com.baibei.accountservice.account.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;
import com.baibei.accountservice.paycenter.dto.request.OrgAssertQuery;
import com.baibei.accountservice.paycenter.dto.request.OrgFeeDetailQuery;
import com.baibei.accountservice.paycenter.dto.response.OrgAssertDetail;
import com.baibei.accountservice.paycenter.dto.response.OrgFeeDetail;

@Service
@Slf4j
public class BalanceBusiness {

    @Autowired
    TAccountCashierLogMapper tAccountCashierLogMapper;

    public Long queryOrgFeeByItem( Date beginDate, Date endDate,String feeItem,String orgId) {
        return tAccountCashierLogMapper.queryOrgTotalFee(beginDate, endDate, feeItem,orgId);
    }
    
    
    public Map<String,OrgFeeDetail> queryOrgFeeDetail(OrgFeeDetailQuery orgFeeDetailQuery){
        List<String>orgIds=orgFeeDetailQuery.getOrgIds();
        Date startDate=orgFeeDetailQuery.getStartDate();
        Date endDate=orgFeeDetailQuery.getEndDate();
        Map<String,OrgFeeDetail> orgFeeMap=new HashMap<String,OrgFeeDetail>();
        for (String orgId : orgIds) {
            OrgFeeDetail orgFeeDetail=new OrgFeeDetail();
            Long loanP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_LOAN_POUNDAGE, orgId);
            Long marginP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_MARGIN_POUNDAGE, orgId);
            Long loanI= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_LOANINTEREST, orgId);
            Long marginI= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_MARGININTEREST, orgId);
            
            Long buyerTP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_BUYTRADE_POUNDAGE, orgId);
            Long sellerTP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_SELLTRADE_POUNDAGE, orgId);
            orgFeeDetail.setLoanPoundage(loanP==null?0L:loanP);
            orgFeeDetail.setMarginPoundage(marginP==null?0L:marginP);
            orgFeeDetail.setLoanInterest(loanI==null?0L:loanI);
            orgFeeDetail.setMarginInterest(marginI==null?0L:marginI);
            orgFeeDetail.setBuerTradePoundage(buyerTP==null?0L:buyerTP);
            orgFeeDetail.setSellerTradePoundage(sellerTP==null?0L:sellerTP);
            orgFeeMap.put(orgId, orgFeeDetail);
        }
        return orgFeeMap;
    }
    
    
    
    public OrgAssertDetail queryOrgAssert(String orgId,Date startDate,Date endDate){
        OrgAssertDetail orgAssertDetail=new OrgAssertDetail();
        Long beginBalance=0L;
        Long endBalance=0L;
        Long rechargeTotal=0L;
        Long withdrawTotal=0L;
        TAccountCashierLogExample endtAccountCashierLogExample=new TAccountCashierLogExample();
        endtAccountCashierLogExample.setOrderByClause("create_time desc");
        endtAccountCashierLogExample.setLimitCount(1);
        TAccountCashierLogExample.Criteria criteria=endtAccountCashierLogExample.createCriteria();
        
        
        TAccountCashierLogExample begintAccountCashierLogExample=new TAccountCashierLogExample();
        begintAccountCashierLogExample.setOrderByClause("create_time asc");
        begintAccountCashierLogExample.setLimitCount(1);
        TAccountCashierLogExample.Criteria bcriteria=begintAccountCashierLogExample.createCriteria();
        
        if(startDate!=null){
            criteria.andCreateTimeGreaterThan(startDate);
            bcriteria.andCreateTimeGreaterThan(startDate);
        }
        if(endDate!=null){
            criteria.andCreateTimeLessThan(endDate);
            bcriteria.andCreateTimeLessThan(endDate);
        }
        criteria.andOrgIdEqualTo(orgId);
        List<TAccountCashierLog> endtAccountCashierLogs= tAccountCashierLogMapper.selectByExample(endtAccountCashierLogExample);
        if(endtAccountCashierLogs!=null&&endtAccountCashierLogs.size()>0){
            TAccountCashierLog tAccountCashierLog=endtAccountCashierLogs.get(0);
            endBalance=tAccountCashierLog.getChangeBefore()+tAccountCashierLog.getChangeAmount();
        }
        
        bcriteria.andOrgIdEqualTo(orgId);
        List<TAccountCashierLog> begintAccountCashierLogs= tAccountCashierLogMapper.selectByExample(begintAccountCashierLogExample);
        if(begintAccountCashierLogs!=null&&begintAccountCashierLogs.size()>0){
            beginBalance=begintAccountCashierLogs.get(0).getChangeBefore();
        }
        orgAssertDetail.setBeginBalance(beginBalance);
        orgAssertDetail.setEndBalance(endBalance);
        rechargeTotal=tAccountCashierLogMapper.queryOrgTotalByFeeItem(startDate, endDate, Constants.FEE_TYPE_RECHARGE, orgId);
        withdrawTotal=tAccountCashierLogMapper.queryOrgTotalByFeeItem(startDate, endDate, Constants.FEE_TYPE_WITHDRAW, orgId);
        orgAssertDetail.setRechargeAmount(rechargeTotal==null?0:rechargeTotal);
        orgAssertDetail.setWithdrawAmount(withdrawTotal==null?0:Math.abs(withdrawTotal));
        
        Long loanP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_LOAN_POUNDAGE, orgId);
        Long marginP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_MARGIN_POUNDAGE, orgId);
        Long loanI= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_LOANINTEREST, orgId);
        Long marginI= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_MARGININTEREST, orgId);
        Long buyerTP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_BUYTRADE_POUNDAGE, orgId);
        Long sellerTP= this.queryOrgFeeByItem(startDate, endDate, Constants.FEE_TYPE_SELLTRADE_POUNDAGE, orgId);
        Long inCome=0L;
        if(loanP!=null){
            inCome+=Math.abs(loanP);
        }
        if(marginP!=null){
            inCome+=Math.abs(marginP);
        }
        if(loanI!=null){
            inCome+=Math.abs(loanI);
        }
        if(marginI!=null){
            inCome+=Math.abs(marginI);
        }
        if(buyerTP!=null){
            inCome+=Math.abs(buyerTP);
        }
        if(sellerTP!=null){
            inCome+=Math.abs(sellerTP);
        }
        orgAssertDetail.setInCome(inCome);
        
        return orgAssertDetail;
    }
    
    public Map<String, OrgAssertDetail> queryOrgAssert(OrgAssertQuery orgAssertQuery) {
        Map<String, OrgAssertDetail> orMap = new HashMap<String, OrgAssertDetail>();
        List<String> orgIds = orgAssertQuery.getOrgIds();
        for (String orgId : orgIds) {
            OrgAssertDetail orgAssertDetail =
                    this.queryOrgAssert(orgId, orgAssertQuery.getStartTime(),
                            orgAssertQuery.getEndTime());
            orMap.put(orgId, orgAssertDetail);
        }
        return orMap;
    }
    
}
