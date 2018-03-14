package com.baibei.accountservice.account.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.dao.TCustomerMapper;
import com.baibei.accountservice.model.TCustomer;
import com.baibei.accountservice.model.TCustomerExample;

@Service
public class CustomerBusiness {

    @Autowired
    TCustomerMapper tCustomerMapper;
    
    //按证件类型+证件号查询客户信息
    public TCustomer qryCustomerByCert(String certType, String certId){
        TCustomerExample example = new TCustomerExample();
        example.createCriteria().andIdTypeEqualTo(certType).andIdCodeEqualTo(certId);
        List<TCustomer> list = tCustomerMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }
}
