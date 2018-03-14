package com.baibei.accountservice.account.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baibei.account.dto.response.BalanceSummary;
import com.baibei.account.dto.response.FeeAndInterest;
import com.baibei.account.dto.response.OrgBalanceSummary;
import com.baibei.account.provider.ReportProvider;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.business.BalanceBusiness;
import com.baibei.accountservice.paycenter.dto.request.OrgAssertQuery;
import com.baibei.accountservice.paycenter.dto.request.OrgFeeDetailQuery;
import com.baibei.accountservice.paycenter.dto.response.OrgAssertDetail;
import com.baibei.accountservice.paycenter.dto.response.OrgFeeDetail;

@Service
public class ReportProviderImpl implements ReportProvider{
	
	@Autowired
	AccountBusiness accountBusiness;
	
	@Autowired
	BalanceBusiness balanceBusiness;
	
	@Override
	public List<BalanceSummary> queryBalanceSumaryList(List<String> userIds, Date startTime, Date endTime) {
		return accountBusiness.queryBalanceSumaryList(userIds, startTime, endTime);
	}

	@Override
	public BalanceSummary queryBalanceSummary(String userId, Date startTime, Date endTime) {
		return accountBusiness.queryBalanceSummary(userId, startTime, endTime);
	}

	@Override
	public FeeAndInterest queryIncome(String userId, Date startTime, Date endTime) {
		return accountBusiness.queryIncome(userId, startTime, endTime);
	}

	@Override
	public List<OrgBalanceSummary> queryBalanceSumaryListByOrgList(
			List<String> orgList, Date startTime, Date endTime) {
		return accountBusiness.queryBalanceSumaryListByOrgList(orgList,startTime,endTime);
	}

	
	@Override
    public Map<String, OrgFeeDetail> queryOrgFeeDetail(OrgFeeDetailQuery orgFeeDetailQuery) {
        return balanceBusiness.queryOrgFeeDetail(orgFeeDetailQuery);
    }

	
	@Override
    public Map<String, OrgAssertDetail> queryOrgAssert(OrgAssertQuery orgAssertQuery) {
        return balanceBusiness.queryOrgAssert(orgAssertQuery);
    }
}
