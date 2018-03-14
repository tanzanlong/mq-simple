package com.baibei.accountservice.account.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.baibei.account.dto.request.BalanceLogQuery;
import com.baibei.account.dto.response.BalanceLog;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.enums.BalanceTypeEnum;
import com.baibei.account.enums.FeeItemEnum;
import com.baibei.account.enums.OrderTypeEnum;
import com.baibei.account.provider.BalanceLogProvider;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class BalanceLogProviderImpl implements BalanceLogProvider {
    
    @Autowired
    private TAccountCashierLogMapper tAccountCashierLogMapper;

    @Override
    public PageResponse<List<BalanceLog>> queryBalanceLog(BalanceLogQuery balanceLogQuery) {

        TAccountCashierLogExample tAccountCashierLogExample = new TAccountCashierLogExample();
        tAccountCashierLogExample.setOrderByClause(" create_time desc ");
        
        TAccountCashierLogExample.Criteria criteria=tAccountCashierLogExample.createCriteria();
        
        if (!StringUtils.isBlank(balanceLogQuery.getUserId())) {
            criteria
                    .andUserIdEqualTo(balanceLogQuery.getUserId());
        }
        criteria.andOrderTypeNotEqualTo(OrderTypeEnum.ORDER_TYPE_FREEZE.getOrderType());
        criteria.andOrderTypeNotEqualTo(OrderTypeEnum.ORDER_TYPE_UNFREEZE.getOrderType());
        
        PageResponse<List<BalanceLog>> balanceLogPage = new PageResponse<List<BalanceLog>>();
        Page<Object> page =
                PageHelper.startPage(balanceLogQuery.getCurrentPage(),
                        balanceLogQuery.getPageSize());
        List<TAccountCashierLog> list =
                tAccountCashierLogMapper.selectByExample(tAccountCashierLogExample);
        List<BalanceLog> balanceLogs = new ArrayList<BalanceLog>(list == null ? 0 : list.size());
        for (int i = 0; i < list.size(); i++) {
            TAccountCashierLog tAccountCashierLog = list.get(i);
            BalanceLog balanceLog = new BalanceLog();
            balanceLog.setBalanceType(tAccountCashierLog.getBalanceType());
            balanceLog.setBalanceTypeDes(BalanceTypeEnum.fromOrderType(tAccountCashierLog.getBalanceType()).getDescription());
            balanceLog.setChangeAmount(tAccountCashierLog.getChangeAmount());
            balanceLog.setChangeBefore(tAccountCashierLog.getChangeBefore());
            balanceLog.setCreateTime(tAccountCashierLog.getCreateTime());
            balanceLog.setFeeItem(tAccountCashierLog.getFeeItem());
            balanceLog.setFeeItemDes(FeeItemEnum.fromOrderType(tAccountCashierLog.getFeeItem()).getRemark());
            balanceLog.setOrderId(tAccountCashierLog.getOrderId());
            balanceLog.setOrderType(tAccountCashierLog.getOrderType());
            balanceLog.setOrderTypeDes(OrderTypeEnum.fromOrderType(tAccountCashierLog.getOrderType()).getDescription());
            balanceLog.setOrgId(tAccountCashierLog.getOrgId());
            balanceLog.setUserId(tAccountCashierLog.getUserId());
            balanceLogs.add(balanceLog);
        }
        balanceLogPage.setCurrentPage(balanceLogQuery.getCurrentPage());
        balanceLogPage.setData(balanceLogs);
        balanceLogPage.setPageSize(balanceLogQuery.getPageSize());
        balanceLogPage.setTotal(page == null ? 0 : page.getTotal());
        return balanceLogPage;
    }

}
