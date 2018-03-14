package com.baibei.account.provider;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baibei.account.dto.response.BalanceSummary;
import com.baibei.account.dto.response.FeeAndInterest;
import com.baibei.account.dto.response.OrgBalanceSummary;
import com.baibei.accountservice.paycenter.dto.request.OrgAssertQuery;
import com.baibei.accountservice.paycenter.dto.request.OrgFeeDetailQuery;
import com.baibei.accountservice.paycenter.dto.response.OrgAssertDetail;
import com.baibei.accountservice.paycenter.dto.response.OrgFeeDetail;

/**
 * 提供报表类查询接口
 * Created by keegan on 12/05/2017.
 */
public interface ReportProvider {
    /**
     * 查询收入
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    FeeAndInterest queryIncome(String userId, Date startTime, Date endTime);

    /**
     * 查询资金汇总-会员资金查询
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    BalanceSummary queryBalanceSummary(String userId, Date startTime, Date endTime);

    /**
     * 查询多个用户的资金汇总
     * @param userIds 用户ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<BalanceSummary> queryBalanceSumaryList(List<String> userIds, Date startTime, Date endTime);
    
    /**
     * 查询多个叶子机构的资金汇总
     * @param orgList
     * @param startTime
     * @param endTime
     * @return
     */
    List<OrgBalanceSummary> queryBalanceSumaryListByOrgList(List<String> orgList, Date startTime, Date endTime);
    

    /**
     * 查询机构手续费明细
     * 
     * @param orgFeeDetailQuery
     * @return
     */
    Map<String, OrgFeeDetail> queryOrgFeeDetail(OrgFeeDetailQuery orgFeeDetailQuery);
    
    
    /**
     * 查询机构手续费明细
     * 
     * @param orgFeeDetailQuery
     * @return
     */
    Map<String, OrgAssertDetail> queryOrgAssert(OrgAssertQuery orgAssertQuery);
}

