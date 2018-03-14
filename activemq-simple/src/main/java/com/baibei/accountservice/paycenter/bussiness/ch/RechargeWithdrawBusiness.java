package com.baibei.accountservice.paycenter.bussiness.ch;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baibei.account.dto.request.QueryTransferRecordsRequest;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.dto.response.TransferRecord;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
@Slf4j
public class RechargeWithdrawBusiness {
    @Autowired
    private TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    
    public PageResponse<List<TransferRecord>> queryRechargeLogByUserId(
            QueryTransferRecordsRequest rechargeSearchRequest) {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.setOrderByClause(" create_time DESC");
        TRechargeWithdrawOrderExample.Criteria criteria = example.createCriteria();
        if (rechargeSearchRequest.getEndTime() != null) {
            criteria.andCreateTimeLessThanOrEqualTo(rechargeSearchRequest.getEndTime());
        }
        if (rechargeSearchRequest.getStartTime() != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(rechargeSearchRequest.getStartTime());
        }

        if (StringUtils.isNotBlank(rechargeSearchRequest.getStatus())) {
            criteria.andStatusEqualTo(rechargeSearchRequest.getStatus());
        }
        if (rechargeSearchRequest.getUserIds() != null
                && rechargeSearchRequest.getUserIds().size() > 0) {
            criteria.andUserIdIn(rechargeSearchRequest.getUserIds());
        }
        if (rechargeSearchRequest.getOrgIds() != null
                && rechargeSearchRequest.getOrgIds().size() > 0) {
            criteria.andOrgIdIn(rechargeSearchRequest.getOrgIds());
        }
        if (!StringUtils.isBlank(rechargeSearchRequest.getOrderId())) {
            criteria.andOrderIdEqualTo(rechargeSearchRequest.getOrderId());
        }
        if (!StringUtils.isBlank(rechargeSearchRequest.getType())) {
            criteria.andOrderTypeEqualTo(rechargeSearchRequest.getType());
        }

        log.info(" queryRechargeLogByUserId :{}", JSON.toJSONString(rechargeSearchRequest));
        Page<Object> page =
                PageHelper.startPage(rechargeSearchRequest.getCurrentPage() <= 0 ? 1
                        : rechargeSearchRequest.getCurrentPage(), rechargeSearchRequest
                        .getPageSize());
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        log.info(" queryRechargeLogByUserId list:{}", JSON.toJSONString(list));
        List<TransferRecord> resList = new ArrayList<TransferRecord>();
        for (TRechargeWithdrawOrder tRechargeOrder : list) {
            resList.add(this.toRechargeResponse(tRechargeOrder));
        }

        log.info(" queryRechargeLogByUserId resList:{}", JSON.toJSONString(resList));
        PageResponse<List<TransferRecord>> pageRspData = new PageResponse<List<TransferRecord>>();
        pageRspData.setCurrentPage(rechargeSearchRequest.getCurrentPage());
        pageRspData.setData(resList);
        pageRspData.setPageSize(rechargeSearchRequest.getPageSize());
        pageRspData.setTotal(page.getTotal());
        return pageRspData;
    }


    public TransferRecord toRechargeResponse(TRechargeWithdrawOrder tRechargeOrder) {
        TransferRecord result = new TransferRecord();
        result.setAmount(tRechargeOrder.getAmount());
        result.setUserId(tRechargeOrder.getUserId());
        result.setStatus(tRechargeOrder.getStatus());
        result.setCreateTime(tRechargeOrder.getCreateTime());
        result.setOrderId(tRechargeOrder.getOrderId());
        result.setSignedAccount(tRechargeOrder.getSignAccountId());
        result.setSignedBank(tRechargeOrder.getSignChannel());
        result.setType(tRechargeOrder.getOrderType());
        return result;
    }
}
