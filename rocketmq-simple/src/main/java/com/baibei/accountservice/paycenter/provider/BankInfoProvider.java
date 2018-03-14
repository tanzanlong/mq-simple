package com.baibei.accountservice.paycenter.provider;

import java.util.List;

import com.baibei.account.dto.request.BankInfoSearch;
import com.baibei.account.dto.response.BankInfo;

public interface BankInfoProvider {
    /**
     * @param tBankInfoExample
     * @return
     */
    public List<BankInfo> queryBankListInfo(BankInfoSearch bankInfoSearch);
}
