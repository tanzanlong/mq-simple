package com.baibei.accountservice.paycenter.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baibei.account.dto.request.BankInfoSearch;
import com.baibei.account.dto.response.BankInfo;
import com.baibei.accountservice.dao.TBankInfoMapper;
import com.baibei.accountservice.model.TBankInfo;
import com.baibei.accountservice.model.TBankInfoExample;
import com.baibei.accountservice.model.TBankInfoExample.Criteria;
import com.baibei.accountservice.paycenter.provider.BankInfoProvider;
@Component
public class BankInfoProviderImpl implements BankInfoProvider{
    @Autowired
    private TBankInfoMapper TBankInfoMapper;
    

    public List<BankInfo> queryBankListInfo(BankInfoSearch bankInfoSearch) {
        if(bankInfoSearch==null){
            bankInfoSearch=new BankInfoSearch(); 
        }
        TBankInfoExample tBankInfoExample = new TBankInfoExample();
        Criteria criteria = tBankInfoExample.createCriteria();

        if (StringUtils.isNotBlank(bankInfoSearch.getBankName())) {
            criteria.andBankNameEqualTo(bankInfoSearch.getBankName());
        }
        if (StringUtils.isNotBlank(bankInfoSearch.getBankNo())) {
            criteria.andBankNoEqualTo(bankInfoSearch.getBankNo());
        }
        List<TBankInfo> tBankInfos = TBankInfoMapper.selectByExample(tBankInfoExample);
        List<BankInfo> bankInfos =
                new ArrayList<BankInfo>(tBankInfos == null ? 0 : tBankInfos.size());
        for (TBankInfo tBankInfo : tBankInfos) {
            BankInfo bankInfo = new BankInfo();
            bankInfo.setBankName(tBankInfo.getBankName());
            bankInfo.setBankNo(tBankInfo.getBankNo());
            bankInfo.setId(tBankInfo.getId());
            bankInfos.add(bankInfo);
        }
        return bankInfos;
    }
}
