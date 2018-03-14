package com.baibei.account.provider;

import java.util.List;

import com.baibei.account.dto.request.DeliveryRequest;
import com.baibei.account.dto.request.FreezeBalanceRequest;
import com.baibei.account.dto.request.LossSettleMatchedRequest;
import com.baibei.account.dto.request.QryTransStatusRequest;
import com.baibei.account.dto.request.SettleLoanFundRequest;
import com.baibei.account.dto.request.SettleLoanInterestRequest;
import com.baibei.account.dto.request.SettleLoanSpotFeeRequest;
import com.baibei.account.dto.request.SettleMatchedRequest;
import com.baibei.account.dto.request.SettleRepaymentRequest;
import com.baibei.account.dto.request.TradeOrderRequest;
import com.baibei.account.dto.request.UnfreezeBalanceRequest;
import com.baibei.account.dto.response.Balance;
import com.baibei.account.dto.response.BalanceAndSignedStatus;
import com.baibei.account.dto.response.QryTransStatusResponse;


/**
 * 提供余额相关接口,包括查询用户余额/余额表更等接口
 * Created by keegan on 11/05/2017.
 */
public interface BalanceProvider {
    /**
     * 查询用户余额
     * @param userId 用户ID
     * @return 余额对象
     */
    Balance queryBalance(String userId);

    /**
     * 查询多个用户的余额
     * @param userIds 用户ID列表
     * @param ignoreZeroAmount True则过滤资产为0的账户
     * @return
     */
    List<BalanceAndSignedStatus> queryBalanceList(List<String> userIds,Boolean ignoreZeroAmount);

    /**
     * 查询用户余额和签约状态
     * @param userId
     * @return 余额和签约状态对象
     */
    BalanceAndSignedStatus queryBalanceAndSignedStatus(String userId);

    /**
     * 资金冻结
     * @param request 请求参数
     */
    void freezeBalance(FreezeBalanceRequest request);

    /**
     * 资金解冻
     * @param request 请求参数
     */
    void unfreezeBalance(UnfreezeBalanceRequest request);

    /**
     * 撮合成交资金结算
     * @param request 请求参数
     */
    void settleMatched(SettleMatchedRequest request);

    /**
     * 融资
     * @param request 请求参数
     */
    void settleLoanFund(SettleLoanFundRequest request);

    /**
     * 融货手续费分成
     * @param request 请求参数
     */
    void settleLoanSpotFee(SettleLoanSpotFeeRequest request);

    /**
     * 还款
     * @param request 请求参数
     */
    void settleRepayment(SettleRepaymentRequest request);

    /**
     * 扣息
     * @param request 请求参数
     */
    void settleLoanInterest(SettleLoanInterestRequest request);
    
    /**
     * 交收
     * @param request 请求参数
     */
    void delivery(DeliveryRequest request);
    
    /**
     * 贸易
     * @param request
     */
    void tradeOrder(TradeOrderRequest request);
    
    /**
     * 强制买货撮合成交资金结算
     * @param request 请求参数
     */
    void lossSettleMatched(LossSettleMatchedRequest request);
    
    /**
     * 查询交易状态
     * @param request
     * @return
     */
    QryTransStatusResponse qryTransStatus(QryTransStatusRequest request);
    
    /**
     * 资金冻结回退(当业务系统调用资金冻结超时时,【无论资金冻结实际成功与否】调用此回退接口进行回退)
     * @param request 请求参数
     */
    void rollbackFreezeBalance(FreezeBalanceRequest request);
    
}
