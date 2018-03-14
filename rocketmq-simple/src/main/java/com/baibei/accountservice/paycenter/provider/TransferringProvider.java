package com.baibei.accountservice.paycenter.provider;

import java.util.List;

import com.baibei.account.dto.request.PayLimitSetRequest;
import com.baibei.account.dto.request.QueryTransferRecordsRequest;
import com.baibei.account.dto.request.TransferRequest;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.dto.response.TransferRecord;
import com.baibei.accountservice.paycenter.dto.response.RechargeResponse;
import com.baibei.accountservice.paycenter.dto.response.WithdrawResponse;
import com.baibei.accountservice.paycenter.exception.PasswordException;
import com.baibei.accountservice.paycenter.exception.PayException;

/**
 * 提供出入金相关接口 Created by keegan on 11/05/2017.
 */
public interface TransferringProvider {

    /**
     * 查询可提余额
     * 
     * @param userId 用户ID
     * @return 可提余额(分)
     */
    Long queryWithdrawableBalance(String userId);

    /**
     * 入金
     * 
     * @param request 请求参数
     */
    RechargeResponse transferIn(TransferRequest request)  throws PayException,PasswordException;

    /**
     * 出金
     * 
     * @param request 请求参数
     */
    WithdrawResponse transferOut(TransferRequest request)  throws PayException,PasswordException;

    /**
     * 查询入金结果
     * 
     * @param serialNo
     * @return DOING:处理中; SUCCESS;成功; FAIL:失败
     */
    String queryTransferInResult(String serialNo);

    /**
     * 查询出金结果
     * 
     * @param serialNo
     * @return DOING:处理中; SUCCESS;成功; FAIL:失败
     */
    String queryTransferOutResult(String serialNo);

    /**
     * 查询出入金流水
     * 
     * @param request 请求参数
     * @return 流水列表
     */
    PageResponse<List<TransferRecord>> queryTransferRecords(QueryTransferRecordsRequest request);
    
    
    
    /**
     * 出入金限制设置
     * 
     * @returnpayLimitSetRequest
     */
    boolean payLimitSet(PayLimitSetRequest payLimitSetRequest);
}
