package com.baibei.accountservice.controller.cb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.business.TicketBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq.Detail;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.ClosePosition;
import com.baibei.accountservice.vo.cb.ClosePositionTicket;
import com.baibei.accountservice.vo.cb.Delivery;
import com.baibei.accountservice.vo.cb.Freeze;
import com.baibei.accountservice.vo.cb.OpenPosition;
import com.baibei.accountservice.vo.cb.OpenPositionTicket;
import com.baibei.accountservice.vo.cb.OrderRollback;

import lombok.extern.slf4j.Slf4j;

/**
 * 跨境余额接口
 * @author peng
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/account/cbbalance")
@Slf4j
public class BalanceCbImplController {
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    TAccountCashierLogMapper tAccountCashierLogMapper;
    
    @Autowired
    TicketBusiness ticketBusiness;
    
    /**
     * 建仓
     * @return
     */
    @RequestMapping(value = "/openPosition")
    public BaseResponse<Boolean> openPosition(@RequestBody OpenPosition openPosition){
        log.info("Open position {}", openPosition);
        try{
            checkParam(openPosition);
            
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(openPosition.getBusinessType());
            req.setOrderId(openPosition.getOrderId());
            req.setOrderType(Constants.ORDER_TYPE_OPENPOSITION);
            List<Detail> detailList = new ArrayList<Detail>();
            Detail detail = new Detail();
            Long accountId = qryAccountIdByUserIdAndCheck(openPosition.getUserId());
            detail.setAccountId(accountId);
            detail.setAmount(-1 * openPosition.getAmount());//建仓支出
            detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail.setFeeItem(Constants.FEE_TYPE_OPENPOSITION);
            detail.setOrgId(openPosition.getOrgId());
            detail.setUserId(openPosition.getUserId());
            detailList.add(detail);
            
            //手续费分成列表
            if(CollectionUtils.isNotEmpty(openPosition.getFeeItemList())){
                for(OpenPosition.FeeItem feeItem : openPosition.getFeeItemList()){
                    Detail feeDetail = new Detail();
                    feeDetail.setAccountId(qryAccountIdByUserIdAndCheck(feeItem.getUserId()));
                    feeDetail.setAmount(feeItem.getFee());
                    feeDetail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                    feeDetail.setFeeItem(Constants.FEE_TYPE_OPENPOSITION_POUNDAGE);
                    feeDetail.setOrgId(feeItem.getOrgId());
                    feeDetail.setUserId(feeItem.getUserId());
                    detailList.add(feeDetail);
                }
            }
            req.setDetailList(detailList);
            return RspUtils.success(accountBusiness.modifyBalance(req));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    /**
     * 平仓
     * @return
     */
    @RequestMapping(value = "/closePosition")
    public BaseResponse<Boolean> closePosition(@RequestBody ClosePosition closePosition){
        log.info("Close position {}", closePosition);
        try{
            checkParam(closePosition);
            
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(closePosition.getBusinessType());
            req.setOrderId(closePosition.getOrderId());
            req.setOrderType(Constants.ORDER_TYPE_CLOSEPOSITION);
            List<Detail> detailList = new ArrayList<Detail>();
            //用户费用变更
            Detail detail = new Detail();
            Long accountId = qryAccountIdByUserIdAndCheck(closePosition.getUserId());
            detail.setAccountId(accountId);
            detail.setAmount(closePosition.getAmount());//平仓收入
            detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail.setFeeItem(Constants.FEE_TYPE_CLOSEPOSITION);
            detail.setOrgId(closePosition.getOrgId());
            detail.setUserId(closePosition.getUserId());
            detailList.add(detail);
            
            //会员费用变更
            long amount = -1 * closePosition.getGain();
            String feeItem = Constants.FEE_TYPE_CLOSEPOSITION_GAIN;
            if(closePosition.getGain() < 0){//用户亏损
                feeItem = Constants.FEE_TYPE_CLOSEPOSITION_LOSS;
            }
            if(amount != 0){
                Detail detail2 = new Detail();
                detail2.setAccountId(qryAccountIdByUserIdAndCheck(closePosition.getMemberUserId()));
                detail2.setAmount(-1 * closePosition.getGain());
                detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail2.setFeeItem(feeItem);
                detail2.setOrgId("");
                detail2.setUserId(closePosition.getMemberUserId());
                detailList.add(detail2);
            }
            req.setDetailList(detailList);
            return RspUtils.success(accountBusiness.modifyBalance(req));
        }catch(Exception e){
            return RspUtils.error(e.getMessage());
        }
    }
    
    /**
     * 交收
     * @return
     */
    @RequestMapping(value = "/delivery")
    public BaseResponse<Boolean> delivery(@RequestBody Delivery delivery){
        log.info("doDelivery {}", delivery);
        try{
            checkParam(delivery);
            
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(delivery.getBusinessType());
            req.setOrderType(Constants.ORDER_TYPE_DELIVERY);
            req.setOrderId(delivery.getOrderId());
            List<Detail> detailList = new ArrayList<Detail>();
            
            //用户费用变更
            Detail detail = new Detail();
            Long accountId = qryAccountIdByUserIdAndCheck(delivery.getUserId());
            detail.setAccountId(accountId);
            detail.setAmount(delivery.getCloseAmount());//平仓收入
            detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail.setFeeItem(Constants.FEE_TYPE_CLOSEPOSITION);
            detail.setOrgId(delivery.getOrderId());
            detail.setUserId(delivery.getUserId());
            detailList.add(detail);
            
            //会员费用变更
            long amount = -1 * delivery.getCloseGain();
            String feeItem = Constants.FEE_TYPE_CLOSEPOSITION_GAIN;
            if(delivery.getCloseGain() < 0){//用户亏损
                feeItem = Constants.FEE_TYPE_CLOSEPOSITION_LOSS;
            }
            if(amount != 0){
                Detail detail2 = new Detail();
                detail2.setAccountId(qryAccountIdByUserIdAndCheck(delivery.getMemberUserId()));
                detail2.setAmount(-1 * delivery.getCloseGain());
                detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail2.setFeeItem(feeItem);
                detail2.setOrgId("");
                detail2.setUserId(delivery.getMemberUserId());
                detailList.add(detail2);
            }
            
            Detail detail3 = new Detail();
            detail3.setAccountId(accountId);
            detail3.setAmount(-1 * delivery.getDeliveryAmount());//交收支出
            detail3.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
            detail3.setFeeItem(Constants.FEE_TYPE_DELIVERY);
            detail3.setOrgId(delivery.getOrgId());
            detail3.setUserId(delivery.getUserId());
            detailList.add(detail3);
            
            req.setDetailList(detailList);
            return RspUtils.success(accountBusiness.modifyBalance(req));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(OpenPosition openPosition){
        if(openPosition.getAmount() == null){
            throw new IllegalArgumentException("parameter amount can not be null");
        }else if(openPosition.getAmount() < 0){
            throw new IllegalArgumentException("parameter amount illegal");
        }
        if(StringUtils.isBlank(openPosition.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(openPosition.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(openPosition.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(StringUtils.isBlank(openPosition.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    }
    
    private void checkParam(ClosePosition closePosition){
        if(closePosition.getAmount() == null){
            throw new IllegalArgumentException("parameter amount can not be null");
        }else if(closePosition.getAmount() < 0){
            throw new IllegalArgumentException("parameter amount illegal");
        }
        if(StringUtils.isBlank(closePosition.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(closePosition.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(closePosition.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(StringUtils.isBlank(closePosition.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(closePosition.getGain() == null){
            throw new IllegalArgumentException("parameter gain can not be null");
        }
    }
    
    private void checkParam(Delivery delivery){
        if(delivery.getDeliveryAmount() == null){
            throw new IllegalArgumentException("parameter deliveryAmount can not be null");
        }else if(delivery.getDeliveryAmount() < 0){
            throw new IllegalArgumentException("parameter deliveryAmount illegal");
        }
        if(StringUtils.isBlank(delivery.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(delivery.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(delivery.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(StringUtils.isBlank(delivery.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(delivery.getCloseAmount() == null){
            throw new IllegalArgumentException("parameter closeAmount can not be null");
        }else if(delivery.getCloseAmount() < 0){
            throw new IllegalArgumentException("parameter closeAmount illegal");
        }
        if(delivery.getCloseGain() == null){
            throw new IllegalArgumentException("parameter closeGain can not be null");
        }
        if(StringUtils.isBlank(delivery.getMemberUserId())){
            throw new IllegalArgumentException("parameter memberUserId can not be blank");
        }
    }
    
    /**
     * 订单余额变更回退
     * @param orderRollback
     * @return
     */
    @RequestMapping(value = "/rollbackOrder")
    public BaseResponse<Boolean> rollbackOrder(@RequestBody OrderRollback orderRollback){
        try{
            //参数检查
            checkParam(orderRollback);
            
            //回退
            return RspUtils.success(doRollback(orderRollback));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(OrderRollback orderRollback){
        if(StringUtils.isBlank(orderRollback.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(orderRollback.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(StringUtils.isBlank(orderRollback.getOrderType())){
            throw new IllegalArgumentException("parameter orderType can not be blank");
        }
    }
    
    private String translateOrderType(String orderTypeRequest){
        if("OPENPOSITION".equalsIgnoreCase(orderTypeRequest)){
            return Constants.ORDER_TYPE_OPENPOSITION;
        }else if("DELIVERY".equalsIgnoreCase(orderTypeRequest)){
            return Constants.ORDER_TYPE_DELIVERY;
        }else if("CLOSEPOSITION".equalsIgnoreCase(orderTypeRequest)){
            return Constants.ORDER_TYPE_CLOSEPOSITION;
        }else{
            throw new IllegalArgumentException("订单类型不正确");
        }
    }
    
    private boolean doRollback(OrderRollback orderRollback) {
        String orderType = translateOrderType(orderRollback.getOrderType());
        String rollbackOrderType = null;
        if(Constants.ORDER_TYPE_OPENPOSITION.equalsIgnoreCase(orderType)){
            rollbackOrderType = Constants.ORDER_TYPE_OPENPOSITION_ROLLBACK;
        }else if(Constants.ORDER_TYPE_DELIVERY.equalsIgnoreCase(orderType)){
            rollbackOrderType = Constants.ORDER_TYPE_DELIVERY_ROLLBACK;
        }else if(Constants.ORDER_TYPE_CLOSEPOSITION.equalsIgnoreCase(orderType)){
            rollbackOrderType = Constants.ORDER_TYPE_CLOSEPOSITION_ROLLBACK;
        }else{
            throw new IllegalArgumentException("不支持的订单类型:" + orderType);
        }
        if(!accountBusiness.checkTransIsNotExists(orderType, orderRollback.getOrderId())){//原单存在
            if(!accountBusiness.checkTransIsNotExists(rollbackOrderType, orderRollback.getOrderId())){//已回退
                return false;
            }else{
                TAccountCashierLogExample example = new TAccountCashierLogExample();
                example.createCriteria().andOrderIdEqualTo(orderRollback.getOrderId()).andOrderTypeEqualTo(orderType);
                List<TAccountCashierLog> list = tAccountCashierLogMapper.selectByExample(example);
                if(CollectionUtils.isNotEmpty(list)){
                    return rollback(orderRollback, list, rollbackOrderType);
                }else{
                    throw new IllegalArgumentException("参数错误，订单无法回退");
                }
            }
        }else{//不存在
           return false;
        }
    }
    
    private boolean rollback(OrderRollback orderRollback, List<TAccountCashierLog> list, String rollbackOrderType){
        AccountBalanceModifyReq req = new AccountBalanceModifyReq();
        req.setBusinessType(orderRollback.getBusinessType());
        req.setOrderId(orderRollback.getOrderId());
        req.setOrderType(rollbackOrderType);
        List<Detail> detailList = new ArrayList<Detail>();
        for (TAccountCashierLog tAccountCashierLog : list){
            Detail detail = new Detail();
            detail.setAccountId(tAccountCashierLog.getAccountId());
            detail.setAmount(-1 * tAccountCashierLog.getChangeAmount());//取反
            detail.setBalanceType(tAccountCashierLog.getBalanceType());
            if(Constants.ORDER_TYPE_OPENPOSITION.equalsIgnoreCase(tAccountCashierLog.getOrderType()) || Constants.ORDER_TYPE_DELIVERY.equalsIgnoreCase(tAccountCashierLog.getOrderType())){
                if(tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_OPENPOSITION)){
                    detail.setFeeItem(Constants.FEE_TYPE_OPENPOSITION_ROLLBACK);
                }else if(tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_OPENPOSITION_POUNDAGE)){
                    detail.setFeeItem(Constants.FEE_TYPE_OPENPOSITION_POUNDAGE_ROLLBACK);
                }else if(tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_DELIVERY)){
                    detail.setFeeItem(Constants.FEE_TYPE_DELIVERY_ROLLBACK);
                }else if((tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_CLOSEPOSITION) ||  tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_CLOSEPOSITION_GAIN) || tAccountCashierLog.getFeeItem().equalsIgnoreCase(Constants.FEE_TYPE_CLOSEPOSITION_LOSS))){
                    detail.setFeeItem(Constants.FEE_TYPE_DELIVERY_ROLLBACK);
                }else{
                    throw new IllegalArgumentException("未知错误，订单额回退失败");
                }
            }else if(Constants.ORDER_TYPE_CLOSEPOSITION.equalsIgnoreCase(tAccountCashierLog.getOrderType())){
                detail.setFeeItem(tAccountCashierLog.getFeeItem());
            }
            detail.setOrgId(tAccountCashierLog.getOrgId());
            detail.setUserId(tAccountCashierLog.getUserId());
            detailList.add(detail);
          
        }
        req.setDetailList(detailList);
        accountBusiness.modifyBalance(req);
        return true;
    }
    
    //按用户ID查账户ID并检查账户是否存在
    private Long qryAccountIdByUserIdAndCheck(String userId){
        Long buyerAccountId = accountBusiness.qryAccountIdByUserId(userId);
        if(buyerAccountId == null){
            throw new IllegalArgumentException("用户ID=[" + userId + "]的账户记录不存在");
        }
        return buyerAccountId;
    }
    
    /**
     * 建仓用券余额变更
     * @return
     */
    @RequestMapping(value = "/openPositionTicket")
    public BaseResponse<Boolean> openPositionTicket(@RequestBody OpenPositionTicket openPositionTicket){
        log.info("Open openPositionTicket {}", openPositionTicket);
        try{
            checkParam(openPositionTicket);
            return RspUtils.success(ticketBusiness.useTickets(Constants.ORDER_TYPE_OPENPOSITION, openPositionTicket.getOrderId(), openPositionTicket.getUserId(), openPositionTicket.getTicketIdList()));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(OpenPositionTicket openPosition){
        if(StringUtils.isBlank(openPosition.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(openPosition.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(openPosition.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(StringUtils.isBlank(openPosition.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(CollectionUtils.isEmpty(openPosition.getTicketIdList())){
            throw new IllegalArgumentException("parameter ticketIdList can not be empty");
        }
    }
    
    /**
     * 用券平仓
     * @return
     */
    @RequestMapping(value = "/closePositionTicket")
    public BaseResponse<Boolean> closePositionTicket(@RequestBody ClosePositionTicket closePositionTicket){
        log.info("Close position {}", closePositionTicket);
        try{
            checkParam(closePositionTicket);
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(closePositionTicket.getBusinessType());
            req.setOrderId(closePositionTicket.getOrderId());
            req.setOrderType(Constants.ORDER_TYPE_CLOSEPOSITION);
            List<Detail> detailList = new ArrayList<Detail>();
            
            if(closePositionTicket.getGain() > 0){
                //用户费用变更
                Detail detail = new Detail();
                Long accountId = qryAccountIdByUserIdAndCheck(closePositionTicket.getUserId());
                detail.setAccountId(accountId);
                detail.setAmount(closePositionTicket.getGain());//平仓收入
                detail.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail.setFeeItem(Constants.FEE_TYPE_CLOSEPOSITION);
                detail.setOrgId(closePositionTicket.getOrgId());
                detail.setUserId(closePositionTicket.getUserId());
                detailList.add(detail);
                
                //会员会用变更
                Detail detail2 = new Detail();
                detail2.setAccountId(qryAccountIdByUserIdAndCheck(closePositionTicket.getMemberUserId()));
                detail2.setAmount(-1 * closePositionTicket.getGain());
                detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail2.setFeeItem(Constants.FEE_TYPE_CLOSEPOSITION_GAIN);
                detail2.setOrgId("");
                detail2.setUserId(closePositionTicket.getMemberUserId());
                detailList.add(detail2);
                req.setDetailList(detailList);
                return RspUtils.success(accountBusiness.modifyBalance(req));
            }else{
                return RspUtils.success(true);
            }
        }catch(Exception e){
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(ClosePositionTicket closePositionTicket){
        if(StringUtils.isBlank(closePositionTicket.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(closePositionTicket.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(closePositionTicket.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(StringUtils.isBlank(closePositionTicket.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(closePositionTicket.getGain() == null){
            throw new IllegalArgumentException("parameter gain can not be null");
        }else if(closePositionTicket.getGain() < 0){
            throw new IllegalArgumentException("parameter gain must bigger than 0");
        }
    }
   
    /**
     * 冻结
     * @return
     */
    @RequestMapping(value = "/freeze")
    public BaseResponse<Boolean> freeze(@RequestBody Freeze freeze){
        log.info("Freeze {}", freeze);
        try{
            checkParam(freeze);
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(freeze.getBusinessType());
            req.setOrderId(freeze.getOrderId());
            req.setOrderType(Constants.ORDER_TYPE_FREEZE);
            Long accountId = qryAccountIdByUserIdAndCheck(freeze.getUserId());
            List<Detail> detailList = new ArrayList<Detail>();
            if(freeze.getAmount() > 0){
                Detail detail = new Detail();
                detail.setAccountId(accountId);
                detail.setAmount(freeze.getAmount());//冻结+
                detail.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
                detail.setFeeItem(Constants.FEE_TYPE_FREEZE);
                detail.setOrgId("");
                detail.setUserId(freeze.getUserId());
                detailList.add(detail);
                
                Detail detail2 = new Detail();
                detail2.setAccountId(accountId);
                detail2.setAmount(-1 * freeze.getAmount());//可用-
                detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail2.setFeeItem(Constants.FEE_TYPE_FREEZE);
                detail2.setOrgId("");
                detail2.setUserId(freeze.getUserId());
                detailList.add(detail2);
                req.setDetailList(detailList);
                return RspUtils.success(accountBusiness.modifyBalance(req));
            }else{
                return RspUtils.success(true);
            }
        }catch(Exception e){
            return RspUtils.error(e.getMessage());
        }
    }
    
    /**
     * 解冻
     * @return
     */
    @RequestMapping(value = "/unfreeze")
    public BaseResponse<Boolean> unfreeze(@RequestBody Freeze freeze){
        log.info("Unfreeze {}", freeze);
        try{
            checkParam(freeze);
            //订单不存在
            if(accountBusiness.checkTransIsNotExists(Constants.ORDER_TYPE_FREEZE, freeze.getOrderId())){
                return RspUtils.success(true);
            }
            AccountBalanceModifyReq req = new AccountBalanceModifyReq();
            req.setBusinessType(freeze.getBusinessType());
            req.setOrderId(freeze.getOrderId());
            req.setOrderType(Constants.ORDER_TYPE_UNFREEZE);
            Long accountId = qryAccountIdByUserIdAndCheck(freeze.getUserId());
            List<Detail> detailList = new ArrayList<Detail>();
            if(freeze.getAmount() > 0){
                Detail detail = new Detail();
                detail.setAccountId(accountId);
                detail.setAmount(-1 * freeze.getAmount());//冻结-
                detail.setBalanceType(Constants.BALANCE_TYPE_FREEZON);
                detail.setFeeItem(Constants.FEE_TYPE_FREEZE);
                detail.setOrgId("");
                detail.setUserId(freeze.getUserId());
                detailList.add(detail);
                
                Detail detail2 = new Detail();
                detail2.setAccountId(accountId);
                detail2.setAmount(freeze.getAmount());//可用+
                detail2.setBalanceType(Constants.BALANCE_TYPE_AVALIABLE);
                detail2.setFeeItem(Constants.FEE_TYPE_FREEZE);
                detail2.setOrgId("");
                detail2.setUserId(freeze.getUserId());
                detailList.add(detail2);
                req.setDetailList(detailList);
                return RspUtils.success(accountBusiness.modifyBalance(req));
            }else{
                return RspUtils.success(true);
            }
        }catch(Exception e){
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(Freeze freeze){
        if(StringUtils.isBlank(freeze.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(freeze.getOrderId())){
            throw new IllegalArgumentException("parameter orderId can not be blank");
        }
        if(StringUtils.isBlank(freeze.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(freeze.getAmount() < 0){
            throw new IllegalArgumentException("parameter amount must bigger than 0");
        }
    }
}
