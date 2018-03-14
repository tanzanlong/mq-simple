package com.baibei.account.provider;

import java.util.List;

import com.baibei.account.dto.request.BalanceLogQuery;
import com.baibei.account.dto.response.BalanceLog;
import com.baibei.account.dto.response.PageResponse;

public interface BalanceLogProvider {
    /**
     * 分页查询帐务流水
     * 
     * <code>com.baibei.accountservice.account.comm.Constants</code>
     * @param balanceLogQuery
     * @return
     */
    public PageResponse<List<BalanceLog>> queryBalanceLog(BalanceLogQuery balanceLogQuery);
}
