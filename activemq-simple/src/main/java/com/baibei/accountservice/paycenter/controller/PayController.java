package com.baibei.accountservice.paycenter.controller;

import java.util.*;

import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample.Criteria;
import com.baibei.accountservice.paycenter.bussiness.RechargeBusiness;
import com.baibei.accountservice.paycenter.bussiness.WithdrawBusiness;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.paycenter.vo.FeeItemRequest;
import com.baibei.accountservice.paycenter.vo.RechargeH5Request;
import com.baibei.accountservice.paycenter.vo.TicketBondsRecharge;
import com.baibei.accountservice.paycenter.vo.WithdrawRequest;
import com.baibei.accountservice.paycenter.vo.WithdrawResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.RechargeWitndrawOrderQryRequest;
import com.baibei.accountservice.vo.cb.RechargeWitndrawOrderQryResponse;
import com.baibei.accountservice.vo.cb.RechargeWitndrawOrderSumResponse;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 出入金接口
 * @author peng
 *
 */
@RestController
@RequestMapping("/account/pay")
@Slf4j
public class PayController {

    @Autowired
    RechargeBusiness rechargeBusiness;

    @Autowired
    WithdrawBusiness withdrawBusiness;

    @Autowired
    AccountBusiness accountBusiness;

    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;

    @Autowired
    TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;

    /**
     * H5支付
     * @param rechargeH5Request
     * @return
     */
    @RequestMapping(value = "/dorechargeh5")
    public BaseResponse<String> dorechargeh5(@RequestBody RechargeH5Request rechargeH5Request){
        log.info("dorechargeh5 {}", rechargeH5Request);
        try{
            //参数判断
            checkParam(rechargeH5Request);

            //找回accountID
            Map<String, Long> userId2AccountId = new HashMap<String, Long>();
            TAccount tAccount = accountBusiness.qryAccountByUserId(rechargeH5Request.getUserId());
            if(tAccount == null){
                throw new IllegalArgumentException("账户不存在");
            }
            rechargeH5Request.setAccountId(tAccount.getAccountId());

            if(!CollectionUtils.isEmpty(rechargeH5Request.getFeeItemList())){
                for(FeeItemRequest feeItemRequest : rechargeH5Request.getFeeItemList()){
                    Long accountId = userId2AccountId.get(feeItemRequest.getUserId());
                    if(accountId == null){
                        TAccount currentTAccount =  accountBusiness.qryAccountByUserId(feeItemRequest.getUserId());
                        if(currentTAccount == null){
                            throw new IllegalArgumentException("用户[" + feeItemRequest.getUserId() + "]账户不存在");
                        }
                        accountId = currentTAccount.getAccountId();
                        userId2AccountId.put(currentTAccount.getUserId(), currentTAccount.getAccountId());
                    }
                    feeItemRequest.setAccountId(accountId);
                }
            }

            //创建订单
            rechargeBusiness.addNewOrder(rechargeH5Request);

            //调用支付网关
            String htmlForm = rechargeBusiness.callRechargeH5Interface(rechargeH5Request);
            return RspUtils.success(htmlForm);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }

    /**
     * 出金
     * @param withdrawRequest
     * @return
     */
    @RequestMapping(value = "/dowithdraw")
    public BaseResponse<WithdrawResponse> dowithdraw(@RequestBody WithdrawRequest withdrawRequest){
        log.info("dowithdraw {}", withdrawRequest);
        try{
            //参数判断
            checkParam(withdrawRequest);


            //找回accountID
            Map<String, Long> userId2AccountId = new HashMap<String, Long>();
            TAccount tAccount = accountBusiness.qryAccountByUserId(withdrawRequest.getUserId());
            if(tAccount == null){
                throw new IllegalArgumentException("账户不存在");
            }
            withdrawRequest.setAccountId(tAccount.getAccountId());

            if(!CollectionUtils.isEmpty(withdrawRequest.getFeeItemList())){
                for(FeeItemRequest feeItemRequest : withdrawRequest.getFeeItemList()){
                    Long accountId = userId2AccountId.get(feeItemRequest.getUserId());
                    if(accountId == null){
                        TAccount currentTAccount =  accountBusiness.qryAccountByUserId(feeItemRequest.getUserId());
                        if(currentTAccount == null){
                            throw new IllegalArgumentException("用户[" + feeItemRequest.getUserId() + "]账户不存在");
                        }
                        accountId = currentTAccount.getAccountId();
                        userId2AccountId.put(currentTAccount.getUserId(), currentTAccount.getAccountId());
                    }
                    feeItemRequest.setAccountId(accountId);
                }
            }


            //创建订单
            withdrawBusiness.addNewOrder(withdrawRequest, false);

            //调用支付网关
            String json = withdrawBusiness.callWithdrawInterface(withdrawRequest);
            WithdrawResponse withdrawResponse = JSON.parseObject(json, WithdrawResponse.class);
            return RspUtils.success(withdrawResponse);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }


    /**
     * 冻结后出金
     * @param withdrawRequest
     * @return
     */
    @RequestMapping(value = "/dowithdrawAfterFreeze")
    public BaseResponse<WithdrawResponse> dowithdrawAfterFreeze(@RequestBody WithdrawRequest withdrawRequest){
        log.info("dowithdrawAfterFreeze {}", withdrawRequest);
        try{
            //参数判断
            checkParam(withdrawRequest);


            //找回accountID
            Map<String, Long> userId2AccountId = new HashMap<String, Long>();
            TAccount tAccount = accountBusiness.qryAccountByUserId(withdrawRequest.getUserId());
            if(tAccount == null){
                throw new IllegalArgumentException("账户不存在");
            }
            withdrawRequest.setAccountId(tAccount.getAccountId());

            if(!CollectionUtils.isEmpty(withdrawRequest.getFeeItemList())){
                for(FeeItemRequest feeItemRequest : withdrawRequest.getFeeItemList()){
                    Long accountId = userId2AccountId.get(feeItemRequest.getUserId());
                    if(accountId == null){
                        TAccount currentTAccount =  accountBusiness.qryAccountByUserId(feeItemRequest.getUserId());
                        if(currentTAccount == null){
                            throw new IllegalArgumentException("用户[" + feeItemRequest.getUserId() + "]账户不存在");
                        }
                        accountId = currentTAccount.getAccountId();
                        userId2AccountId.put(currentTAccount.getUserId(), currentTAccount.getAccountId());
                    }
                    feeItemRequest.setAccountId(accountId);
                }
            }


            //创建订单
            withdrawBusiness.addNewOrder(withdrawRequest, true);

            //调用支付网关
            String json = withdrawBusiness.callWithdrawInterface(withdrawRequest);
            WithdrawResponse withdrawResponse = JSON.parseObject(json, WithdrawResponse.class);
            return RspUtils.success(withdrawResponse);
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }

    private void checkParam(RechargeH5Request rechargeH5Request){
        if(rechargeH5Request.getAmount() == null){
            throw new IllegalArgumentException("parameter amount can not be null");
        }
        if(rechargeH5Request.getAmount() < 1){
            throw new IllegalArgumentException("parameter amount can not less then 1");
        }
     /*   if(rechargeH5Request.getBankAccount() == null){
            throw new IllegalArgumentException("parameter bankAccount can not be null");
        }*/
        if(StringUtils.isBlank(rechargeH5Request.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(rechargeH5Request.getCallbackUrl())){
            throw new IllegalArgumentException("parameter callbackUrl can not be blank");
        }
        if(StringUtils.isBlank(rechargeH5Request.getChannelCode())){
            throw new IllegalArgumentException("parameter channelCode can not be blank");
        }
        if(StringUtils.isBlank(rechargeH5Request.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(StringUtils.isBlank(rechargeH5Request.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }

        if(PayCenterConstant.BUSINESS_TYPE_CB.equals(rechargeH5Request.getBusinessType()) &&
                PayCenterConstant.PAY_CODE_ZNPAYH5.equals(rechargeH5Request.getChannelCode())){
            if(StringUtils.isBlank(rechargeH5Request.getName())){
                throw new IllegalArgumentException("parameter name can not be blank");
            }
            if(StringUtils.isBlank(rechargeH5Request.getPhone())){
                throw new IllegalArgumentException("parameter phone can not be blank");
            }
            if(StringUtils.isBlank(rechargeH5Request.getCertNo())){
                throw new IllegalArgumentException("parameter certNo can not be blank");
            }
        }
    }

    private void checkParam(WithdrawRequest withdrawRequest){
        if(withdrawRequest.getAmount() == null){
            throw new IllegalArgumentException("parameter amount can not be null");
        }
        if(withdrawRequest.getBank() == null){
            throw new IllegalArgumentException("parameter bank can not be null");
        }
        if(StringUtils.isBlank(withdrawRequest.getBankAccount())){
            throw new IllegalArgumentException("parameter bankAccount can not be blank");
        }
      /*  if(withdrawRequest.getBranchBankName() == null){
            throw new IllegalArgumentException("parameter branchBankName can not be null");
        }*/
        if(StringUtils.isBlank(withdrawRequest.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(withdrawRequest.getChannelCode())){
            throw new IllegalArgumentException("parameter channelCode can not be blank");
        }
     /*   if(withdrawRequest.getCity() == null){
            throw new IllegalArgumentException("parameter city can not be null");
        }*/
        if(StringUtils.isBlank(withdrawRequest.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
       /* if(withdrawRequest.getProvince() == null){
            throw new IllegalArgumentException("parameter province can not be null");
        }*/
        if(StringUtils.isBlank(withdrawRequest.getRealName())){
            throw new IllegalArgumentException("parameter realName can not be blank");
        }
        if(StringUtils.isBlank(withdrawRequest.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(withdrawRequest.getAmount() < 1){
            throw new IllegalArgumentException("parameter amount can not less then 1");
        }
    }

    private void checkParam(TicketBondsRecharge ticketBondsRecharge){
        if(StringUtils.isBlank(ticketBondsRecharge.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(ticketBondsRecharge.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(ticketBondsRecharge.getAmount() == null){
            throw new IllegalArgumentException("parameter amount can not be null");
        }
    }

    /**
     *
     * @param ticketBondsRecharge
     * @return
     */
    @RequestMapping(value = "/ticketBondsRecharge")
    public BaseResponse<Boolean> ticketBondsRecharge(@RequestBody TicketBondsRecharge ticketBondsRecharge){
        log.info("ticketBondsRecharge {}", ticketBondsRecharge);
        try{
            //参数判断
            checkParam(ticketBondsRecharge);

            //找回accountID
            TAccount tAccount = accountBusiness.qryAccountByUserId(ticketBondsRecharge.getUserId());
            if(tAccount == null){
                throw new IllegalArgumentException("账户不存在");
            }

            TAccountBalanceExample example = new TAccountBalanceExample();
            example.createCriteria().andAccountIdEqualTo(tAccount.getAccountId()).andBalanceTypeEqualTo(Constants.BALANCE_TYPE_TICKET_BONDS);
            List<TAccountBalance> balanceList = tAccountBalanceMapper.selectByExample(example);
            if(CollectionUtils.isEmpty(balanceList)){
                throw new IllegalArgumentException("余额记录不存在");
            }

            TAccountBalance tAccountBalance = balanceList.get(0);
            tAccountBalance.setAmount(tAccountBalance.getAmount() + ticketBondsRecharge.getAmount());
            tAccountBalanceMapper.updateByPrimaryKey(tAccountBalance);
            return RspUtils.success(true);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/qryRechargeWithdrawOrders")
    public BaseResponse<List<RechargeWitndrawOrderQryResponse>> qryRechargeWithdrawOrders(@RequestBody RechargeWitndrawOrderQryRequest request){
        log.info("qryRechargeWithdrawOrders {}", request);
        try{
            TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
            Criteria criteria = example.createCriteria();
            if(request.getStartTime() != null){
                criteria.andCreateTimeGreaterThanOrEqualTo(request.getStartTime());
            }
            if(request.getEndTime() != null){
                if(DateUtils.isSameDay(request.getStartTime(),request.getEndTime())){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(request.getEndTime());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    request.setEndTime(calendar.getTime());
                }
                criteria.andCreateTimeLessThanOrEqualTo(request.getEndTime());
            }
            if(request.getUserId() != null){
                criteria.andUserIdEqualTo(request.getUserId());
            }
            if(request.getOrgId() != null){
                criteria.andOrgIdEqualTo(request.getOrgId());
            }
            if(request.getOrderType() != null){
                criteria.andOrderTypeEqualTo(request.getOrderType());
            }
            if(request.getOrderStatus() != null){
                criteria.andStatusEqualTo(request.getOrderStatus());
            }
            if(request.getOrderId() != null){
                criteria.andOrderIdEqualTo(request.getOrderId());
            }
            if(request.getChannelCode() != null){
                criteria.andSignChannelEqualTo(request.getChannelCode());
            }
            if(request.getUserIdList() != null){
                criteria.andUserIdIn(request.getUserIdList());
            }
            Page<TRechargeWithdrawOrder> page = PageHelper.startPage(request.getPageNo(), request.getPageSize());
            List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
            List<RechargeWitndrawOrderQryResponse> responseList = new ArrayList<RechargeWitndrawOrderQryResponse>();
            if(!CollectionUtils.isEmpty(list)){
                for(TRechargeWithdrawOrder tRechargeWithdrawOrder : list){
                    responseList.add(toRechargeWitndrawOrderQryResponse(tRechargeWithdrawOrder));
                }
            }
            return RspUtils.success(responseList,page.getTotal());
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/sumRechargeWithdrawOrders")
    public BaseResponse<RechargeWitndrawOrderSumResponse> sumRechargeWithdrawOrders(@RequestBody RechargeWitndrawOrderQryRequest request){
        log.info("sumRechargeWithdrawOrders {}", request);
        try{
            Map<String, Object> params = new HashMap<String, Object>();
            if(request.getStartTime() != null){
                params.put("startTime", request.getStartTime());
            }
            if(request.getEndTime() != null){
                if(DateUtils.isSameDay(request.getStartTime(),request.getEndTime())){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(request.getEndTime());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    request.setEndTime(calendar.getTime());
                }
                params.put("endTime", request.getEndTime());
            }

            if(request.getUserId() != null){
                params.put("userId", request.getUserId());
            }
            if(request.getOrgId() != null){
                params.put("orgId", request.getOrgId());
            }
            if(request.getOrderType() != null){
                params.put("orderType", request.getOrderType());
            }
            if(request.getOrderId() != null){
                params.put("orderId", request.getOrderId());
            }
            if(request.getChannelCode() != null){
                params.put("signChannel", request.getChannelCode());
            }
            if(request.getBusinessType() != null){
                params.put("businessType", request.getBusinessType());
            }
            if(request.getUserIdList() != null){
                params.put("userIdList", request.getUserIdList());
            }

            Long doingAmount = 0L;
            Long successAmount = 0L;
            Long failAmount = 0L;
            if(request.getOrderStatus() != null){
                params.put("orderStatus", request.getOrderStatus());
                if(Constants.STATUS_DOING.equals(request.getOrderStatus())){
                    doingAmount = tRechargeWithdrawOrderMapper.sumAmount(params);
                }else if (Constants.STATUS_SUCCESS.equals(request.getOrderStatus())){
                    successAmount = tRechargeWithdrawOrderMapper.sumAmount(params);
                }else if(Constants.STATUS_FAIL.equals(request.getOrderStatus())){
                    failAmount = tRechargeWithdrawOrderMapper.sumAmount(params);
                }
            }else{
                params.put("orderStatus", Constants.STATUS_DOING);
                doingAmount = tRechargeWithdrawOrderMapper.sumAmount(params);

                params.put("orderStatus", Constants.STATUS_SUCCESS);
                successAmount = tRechargeWithdrawOrderMapper.sumAmount(params);

                params.put("orderStatus", Constants.STATUS_FAIL);
                failAmount = tRechargeWithdrawOrderMapper.sumAmount(params);
            }
            RechargeWitndrawOrderSumResponse response = new RechargeWitndrawOrderSumResponse();

            response.setDoingAmount(doingAmount==null ? 0:doingAmount);
            response.setSuccessAmount(successAmount==null ? 0:successAmount);
            response.setFailAmount(failAmount==null ? 0:failAmount);
            response.setTotalAmount(response.getDoingAmount() + response.getSuccessAmount() + response.getFailAmount());

            return RspUtils.success(response);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }

    private RechargeWitndrawOrderQryResponse toRechargeWitndrawOrderQryResponse(TRechargeWithdrawOrder tRechargeWithdrawOrder){
        RechargeWitndrawOrderQryResponse response = new RechargeWitndrawOrderQryResponse();
        response.setAmount(tRechargeWithdrawOrder.getAmount());
        response.setChannelCode(tRechargeWithdrawOrder.getSignChannel());
        response.setCreateTime(tRechargeWithdrawOrder.getCreateTime());
        response.setOrderId(tRechargeWithdrawOrder.getOrderId());
        response.setOrderStatus(tRechargeWithdrawOrder.getStatus());
        response.setOrderType(tRechargeWithdrawOrder.getOrderType());
        response.setOrgId(tRechargeWithdrawOrder.getOrgId());
        response.setUpdateTime(tRechargeWithdrawOrder.getUpdateTime());
        response.setUserId(tRechargeWithdrawOrder.getUserId());
        return response;
    }
}