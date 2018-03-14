package com.baibei.accountservice.account.business;

import org.springframework.beans.factory.annotation.Autowired;

import com.baibei.account.dto.request.BalanceLogQuery;
import com.baibei.accountservice.dao.TAccountCashierOrderMapper;

public class BalanceLogBusiness {
    
    @Autowired
    private TAccountCashierOrderMapper tAccountCashierOrderMapper;
    
    public void queryBalanceLog(BalanceLogQuery balanceLogQuery) {
        
    }
}
