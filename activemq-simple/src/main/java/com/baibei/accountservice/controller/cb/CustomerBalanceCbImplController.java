package com.baibei.accountservice.controller.cb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.CustomerBalnceQryRequest;
import com.baibei.accountservice.vo.cb.CustomerBalnceQryResponse;
import com.baibei.accountservice.vo.cb.CustomerBalnceSumResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@EnableAutoConfiguration
@RequestMapping("/account/cbcustomerbalance")
@Slf4j
public class CustomerBalanceCbImplController {

    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;
    
    @RequestMapping(value = "/qryCustomerBalnce")
    public BaseResponse<List<CustomerBalnceQryResponse>> qryCustomerBalnce(@RequestBody CustomerBalnceQryRequest req){
        try{
            checkParam(req);
            List<CustomerBalnceQryResponse> responseList = new ArrayList<CustomerBalnceQryResponse>();
            TAccountBalanceExample example = new TAccountBalanceExample();
            TAccountBalanceExample.Criteria criteria = example.createCriteria();
            if(CollectionUtils.isNotEmpty(req.getUserIdList())){
                criteria.andUserIdIn(req.getUserIdList());
            }
            List<String> balanceTypeList = new ArrayList<String>();
            balanceTypeList.add(Constants.BALANCE_TYPE_AVALIABLE);
            balanceTypeList.add(Constants.BALANCE_TYPE_FREEZON);
            criteria.andBalanceTypeIn(balanceTypeList);
            List<TAccountBalance> accountBalanceList = tAccountBalanceMapper.selectByExample(example);
            Map<String, CustomerBalnceQryResponse> userId2Balance = new HashMap<String, CustomerBalnceQryResponse>();
            if(CollectionUtils.isNotEmpty(accountBalanceList)){
                for(TAccountBalance tAccountBalance : accountBalanceList){
                    CustomerBalnceQryResponse response = toCustomerBalnceQryResponse(tAccountBalance);
                    if(response != null){
                        CustomerBalnceQryResponse oldResponse = userId2Balance.get(response.getUserId());
                        if(oldResponse != null){
                            if(response.getAvaliableAmount() != null){
                                oldResponse.setAvaliableAmount(response.getAvaliableAmount());
                            }else{
                                oldResponse.setFreezeAmount(response.getFreezeAmount());
                            }
                        }else{
                            userId2Balance.put(response.getUserId(), response);
                        }
                    }
                }
            }
            responseList.addAll(userId2Balance.values());
            return RspUtils.success(responseList);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private CustomerBalnceQryResponse toCustomerBalnceQryResponse(TAccountBalance tAccountBalance){
        CustomerBalnceQryResponse response = new CustomerBalnceQryResponse();
        if(Constants.BALANCE_TYPE_AVALIABLE.equalsIgnoreCase(tAccountBalance.getBalanceType())){
            response.setAvaliableAmount(tAccountBalance.getAmount());
        }else if(Constants.BALANCE_TYPE_FREEZON.equalsIgnoreCase(tAccountBalance.getBalanceType())){
            response.setFreezeAmount(tAccountBalance.getAmount());
        }else{
            return null;
        }
        response.setUserId(tAccountBalance.getUserId());
        return response;
    }
    
    @RequestMapping(value = "/sumCustomerBalnce")
    public BaseResponse<CustomerBalnceSumResponse> sumCustomerBalnce(@RequestBody CustomerBalnceQryRequest req){
        try{
            checkParam(req);
            CustomerBalnceSumResponse response = new CustomerBalnceSumResponse();
            Map<String, Object> params = new HashMap<String, Object>();
            if(CollectionUtils.isNotEmpty(req.getUserIdList())){
                params.put("list", req.getUserIdList());
            }
            params.put("balanceType", Constants.BALANCE_TYPE_AVALIABLE);
            Long avaliableAmount = tAccountBalanceMapper.sumAmount(params);
            response.setTotalAvaliableAmount(avaliableAmount==null ? 0:avaliableAmount);
            params.put("balanceType", Constants.BALANCE_TYPE_FREEZON);
            Long freezeAmount = tAccountBalanceMapper.sumAmount(params);
            response.setTotalFreezeAmount(freezeAmount==null ? 0:freezeAmount);
            return RspUtils.success(response);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(CustomerBalnceQryRequest req){
        if(CollectionUtils.isEmpty(req.getUserIdList())){
            throw new IllegalArgumentException("parameter userIdList can not be empty");
        }
    }
    
}
