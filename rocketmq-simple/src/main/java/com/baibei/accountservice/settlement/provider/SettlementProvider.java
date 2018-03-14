package com.baibei.accountservice.settlement.provider;

import java.util.List;

import com.baibei.accountservice.settlement.dto.ClearResult;
import com.baibei.accountservice.settlement.dto.SettleResult;
import com.baibei.accountservice.settlement.dto.SettleResultQuery;

public interface SettlementProvider {

    /**
     * @return
     */
    public ClearResult  queryClearResult();
    

    /**
     * @return
     */
    public  List<SettleResult>  querySettleResult(SettleResultQuery settleResultQuery);
}
