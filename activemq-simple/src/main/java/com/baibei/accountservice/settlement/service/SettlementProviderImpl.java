package com.baibei.accountservice.settlement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baibei.accountservice.dao.TDailySettlementResMapper;
import com.baibei.accountservice.model.TDailySettlementRes;
import com.baibei.accountservice.model.TDailySettlementResExample;
import com.baibei.accountservice.settlement.dto.ClearResult;
import com.baibei.accountservice.settlement.dto.SettleResult;
import com.baibei.accountservice.settlement.dto.SettleResultQuery;
import com.baibei.accountservice.settlement.provider.SettlementProvider;
import com.baibei.accountservice.util.DateUtil;
@Service
public class SettlementProviderImpl implements SettlementProvider{
    
    @Autowired
    TDailySettlementResMapper tDailySettlementResMapper;

    @Override
    public ClearResult queryClearResult() {
      /** 
        clearResult.setClearDate("2017-06-29");
        clearResult.setStatus("SUCCESS");
        clearResult.setTime("22:45");**/
        ClearResult clearResult=new ClearResult();
        TDailySettlementResExample tDailySettlementResExample=new TDailySettlementResExample();
        tDailySettlementResExample.setOrderByClause(" ID DESC ");
        List<TDailySettlementRes> tDailySettlementRess= tDailySettlementResMapper.selectByExample(tDailySettlementResExample);
        if(tDailySettlementRess!=null&&tDailySettlementRess.size()>0){
            TDailySettlementRes tDailySettlementRes=tDailySettlementRess.get(0);
            String dateStr=DateUtil.format(tDailySettlementRes.getUpdateTime(), DateUtil.FORMAT_0);
            String clearDate=dateStr.substring(0, 10);
            clearResult.setClearDate(clearDate);
            clearResult.setStatus(tDailySettlementRes.getSettlementRes());
            clearResult.setTime(dateStr.substring(11, 16)); 
        }
        return clearResult;
    }

    @Override
    public List<SettleResult> querySettleResult(SettleResultQuery settleResultQuery) {
        // TODO Auto-generated method stub
        List<SettleResult>  SettleResults=new ArrayList<SettleResult>();
        SettleResult settleResult=new SettleResult();
        Date now=new Date();
        settleResult.setBankAccount("35125154325634");
        settleResult.setBankCode("CBC");
        settleResult.setBankName("农商行");
        settleResult.setSettleEndTime(now);
        settleResult.setSettleStartTime(now);
        settleResult.setSettleStatus("SUCCESS");
        settleResult.setSignInTime(now);
        settleResult.setSignOutTime(now);
        settleResult.setTradAccount("12535436236");
        settleResult.setTradAmount("100");
        SettleResults.add(settleResult);
        return SettleResults;
    }
}
