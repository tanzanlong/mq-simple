package com.baibei.accountservice.controller;

import java.util.*;

import com.github.pagehelper.Page;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;
import com.baibei.accountservice.model.TAccountCashierLogExample.Criteria;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.BalanceLogQryRequest;
import com.baibei.accountservice.vo.BalanceLogQryResponse;
import com.baibei.accountservice.vo.cb.CustomerBalanceLogQryRequest;
import com.baibei.accountservice.vo.cb.CustomerBalanceLogQryResponse;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 资金流水接口
 * @author peng
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/account/balancelog")
@Slf4j
public class BalanceLogController {
    
    @Autowired
    TAccountCashierLogMapper tAccountCashierLogMapper;
    
    /**
     * 资金流水查询
     * @param req
     * @return
     */
    @RequestMapping(value = "/qryloglist")
    public BaseResponse<List<BalanceLogQryResponse>> qryloglist(@RequestBody BalanceLogQryRequest req){
        try{
            List<BalanceLogQryResponse> resultList = new ArrayList<BalanceLogQryResponse>();
           
            //参数检查
            checkParam(req);
            
            //第一步，分页查询出订单
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("beginDate", req.getStartTime());
            param.put("endDate", req.getEndTime());
            param.put("offset", req.getPageNo() * req.getPageSize());
            param.put("size", req.getPageSize());
            param.put("list", req.getUserIds());
            List<TAccountCashierLog> orderList = tAccountCashierLogMapper.queryOrderIdPageByUserId(param);
            
            Set<String> orderIdSet = new HashSet<String>();
            Map<String, String> orderIdAndTypeMap = new HashMap<String, String>();
            if(CollectionUtils.isNotEmpty(orderList)){
                for(TAccountCashierLog tAccountCashierLog : orderList){
                    orderIdSet.add(tAccountCashierLog.getOrderId());
                    String key = tAccountCashierLog.getOrderType() + "$" + tAccountCashierLog.getOrderId();
                    orderIdAndTypeMap.put(key, "");
                }
            }
            
            //第二步，按订单查询流水日志
            if(CollectionUtils.isNotEmpty(orderIdSet)){
                TAccountCashierLogExample example = new TAccountCashierLogExample();
                example.createCriteria().andUserIdIn(req.getUserIds()).andCreateTimeGreaterThanOrEqualTo(req.getStartTime()).andCreateTimeLessThanOrEqualTo(req.getEndTime()).andOrderIdIn(new ArrayList<>(orderIdSet)).andBalanceTypeEqualTo(Constants.BALANCE_TYPE_AVALIABLE);
                example.setOrderByClause("id desc");
                List<TAccountCashierLog> list = tAccountCashierLogMapper.selectByExample(example);
                if(!CollectionUtils.isEmpty(list)){
                    list  = mergeByOrderIdAndType(list);
                    for(TAccountCashierLog tAccountCashierLog : list){
                        //过滤掉订单号相同，订单类型不同的记录
                        String key = tAccountCashierLog.getOrderType() + "$" + tAccountCashierLog.getOrderId();
                        if(orderIdAndTypeMap.containsKey(key)){
                            resultList.add(toBalanceLogQryResponse(req.getBusinessType(), tAccountCashierLog));
                        }
                    }
                }
            }
            
            Collections.sort(resultList);
            return RspUtils.success(resultList);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    //按订单类型和订单号合并相同的订单流水
    private List<TAccountCashierLog> mergeByOrderIdAndType(List<TAccountCashierLog> list){
        Map<String, TAccountCashierLog> map = new HashMap<String, TAccountCashierLog>();
        for(TAccountCashierLog tAccountCashierLog : list){
            String key = tAccountCashierLog.getOrderType() + "$" + tAccountCashierLog.getOrderId();
            TAccountCashierLog oldTAccountCashierLog = map.get(key);
            if(oldTAccountCashierLog != null){
                oldTAccountCashierLog.setChangeAmount(tAccountCashierLog.getChangeAmount() + oldTAccountCashierLog.getChangeAmount());
                oldTAccountCashierLog.setChangeBefore(tAccountCashierLog.getChangeBefore());
            }else{
                map.put(key, tAccountCashierLog);
            }
        }
        return new ArrayList<>(map.values());
    }
    
    private BalanceLogQryResponse toBalanceLogQryResponse(String businessType, TAccountCashierLog tAccountCashierLog){
        BalanceLogQryResponse response = new BalanceLogQryResponse();
        response.setAmount(tAccountCashierLog.getChangeAmount());
        response.setCreateTime(tAccountCashierLog.getCreateTime());
        response.setLeftAmount(tAccountCashierLog.getChangeAmount() + tAccountCashierLog.getChangeBefore());
        response.setOrderId(tAccountCashierLog.getOrderId());
        response.setUserId(tAccountCashierLog.getUserId());
        response.setOrderType(translate(businessType, tAccountCashierLog.getOrderType()));
        //翻译订单类型
        return response;
    }
    
    //翻译订单类型
    private String translate(String businessType, String orderType){
        if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_RECHARGE)){
            return "RECHARGE";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_WITHDRAW)){
            return "WITHDRAW";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_OPENPOSITION)){
            return "OPENPOSITION";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_CLOSEPOSITION)){
            return "CLOSEPOSITION";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_DELIVERY)){
            return "DELIVERY";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_RECHARGE_ROLLBACK)){
            return "RECHARGE_ROLLBACK";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_WITHDRAW_ROLLBACK)){
            return "WITHDRAW_ROLLBACK";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_OPENPOSITION_ROLLBACK)){
            return "OPENPOSITION_ROLLBACK";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_CLOSEPOSITION_ROLLBACK)){
            return "CLOSEPOSITION_ROLLBACK";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_DELIVERY_ROLLBACK)){
            return "DELIVERY_ROLLBACK";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_FREEZE)){
            return "WITHDRAW";
        }else if(orderType.equalsIgnoreCase(Constants.ORDER_TYPE_UNFREEZE)){
            return "WITHDRAW_ROLLBACK";
        }
        return "OTHER";
    }
  
    private void checkParam(BalanceLogQryRequest req){
        if(StringUtils.isBlank(req.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(req.getEndTime() == null){
            throw new IllegalArgumentException("parameter endTime can not be null");
        }
        if(req.getStartTime() == null){
            throw new IllegalArgumentException("parameter startTime can not be null");
        }
        if(CollectionUtils.isEmpty(req.getUserIds())){
            throw new IllegalArgumentException("parameter userIds can not be empty");
        }
        if(req.getPageNo() == null){
            throw new IllegalArgumentException("parameter pageNo can not be null");
        }
        if(req.getPageSize() == null){
            throw new IllegalArgumentException("parameter pageSize can not be null");
        }
    }
    
    @RequestMapping(value = "/qryCustomerBalanceLog")
    public BaseResponse<List<CustomerBalanceLogQryResponse>> qryCustomerBalanceLog(@RequestBody CustomerBalanceLogQryRequest req){
        log.info("qryCustomerBalanceLog {}", req);
        List<CustomerBalanceLogQryResponse> responseList = null;
        try {
            TAccountCashierLogExample example = new TAccountCashierLogExample();
            Criteria criteria = example.createCriteria();
            if(!StringUtils.isBlank(req.getInOutType())){
                if("IN".equalsIgnoreCase(req.getInOutType())){
                    criteria.andChangeAmountGreaterThan(0L);
                }else{
                    criteria.andChangeAmountLessThan(0L);
                }
            }
            if(!StringUtils.isBlank(req.getOrderId())){
               criteria.andOrderIdEqualTo(req.getOrderId());
            }
            if(!StringUtils.isBlank(req.getOrderType())){
                criteria.andOrderTypeEqualTo(req.getOrderType());
            }
            if(!StringUtils.isBlank(req.getOrgId())){
                criteria.andOrgIdEqualTo(req.getOrgId());
            }
            if(!StringUtils.isBlank(req.getUserId())){
                criteria.andUserIdEqualTo(req.getUserId());
            }
            if(req.getStartTime() != null){
                criteria.andCreateTimeGreaterThanOrEqualTo(req.getStartTime());
            }
            if(req.getEndTime() != null){
                if(DateUtils.isSameDay(req.getStartTime(),req.getEndTime())){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(req.getEndTime());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    req.setEndTime(calendar.getTime());
                }
                criteria.andCreateTimeLessThanOrEqualTo(req.getEndTime());
            }
            if(req.getUserIdList() != null){
                criteria.andUserIdIn(req.getUserIdList());
            }
            criteria.andBalanceTypeEqualTo(Constants.BALANCE_TYPE_AVALIABLE);
            Page<TAccountCashierLog> page = PageHelper.startPage(req.getPageNo(), req.getPageSize());
            List<TAccountCashierLog> logList = tAccountCashierLogMapper.selectByExample(example);
            responseList = new ArrayList<CustomerBalanceLogQryResponse>();
            if(CollectionUtils.isNotEmpty(logList)){
                for(TAccountCashierLog log : logList){
                    responseList.add(toCustomerBalanceLogQryResponse(log));
                }
            }
            return RspUtils.success(responseList,page.getTotal());
        } catch (Exception e) {
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private CustomerBalanceLogQryResponse toCustomerBalanceLogQryResponse(TAccountCashierLog tAccountCashierLog){
        CustomerBalanceLogQryResponse response = new CustomerBalanceLogQryResponse();
        response.setAmount(Math.abs(tAccountCashierLog.getChangeAmount()));
        response.setCreateTime(tAccountCashierLog.getCreateTime());
        String inOutType = "IN";
        if(tAccountCashierLog.getChangeAmount() < 0){
            inOutType = "OUT";
        }
        response.setInOutType(inOutType);
        response.setOrderId(tAccountCashierLog.getOrderId());
        response.setOrderType(tAccountCashierLog.getOrderType());
        response.setOrgId(tAccountCashierLog.getOrgId());
        response.setUserId(tAccountCashierLog.getUserId());

        response.setBalance(tAccountCashierLog.getChangeAmount() + tAccountCashierLog.getChangeBefore());
        response.setUpdateTime(tAccountCashierLog.getUpdateTime());
        return response;
    }
}
