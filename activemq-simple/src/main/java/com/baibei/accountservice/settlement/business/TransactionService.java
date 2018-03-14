package com.baibei.accountservice.settlement.business;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baibei.accountservice.dao.TAppConfigMapper;
import com.baibei.accountservice.dao.TFourElementMapper;
import com.baibei.accountservice.model.TAppConfig;
import com.baibei.accountservice.model.TFourElement;
@Service
public class TransactionService {
     @Autowired
     TAppConfigMapper tAppConfigMapper;
     
     @Autowired
     TFourElementMapper tFourElementMapper;
     
     @Transactional(propagation=Propagation.REQUIRES_NEW)
     public void save() throws Exception{
         Date now=new Date();
         TAppConfig tAppConfig=new TAppConfig();
         tAppConfig.setAppId("transaction1");
         tAppConfig.setAppSrcret("transaction1");
         tAppConfig.setCreateTime(now);
         tAppConfig.setRechargeNotifyUrl("recharge url");
         tAppConfig.setStatus(1);
         tAppConfig.setUpdateTime(now);
         tAppConfig.setWithdrawNotifyUrl("withdraw url");
         TFourElement tFourElement=new TFourElement();
         tFourElement.setBankCard("transaction2");
         tFourElement.setCreateTime(now);
         tFourElement.setCustomerId(now.getTime());
         tFourElement.setCustomerName("transaction2");
         tFourElement.setIdCode("transaction2");
         tFourElement.setIsDel(1);
         tFourElement.setMobile("transaction2");
         tFourElement.setUpdateTime(now);
         this.saveConfig(tAppConfig);
         this.saveElement(tFourElement);
     }
     
     @Transactional(propagation=Propagation.NOT_SUPPORTED)
     public void saveConfig(TAppConfig tAppConfig){
         tAppConfigMapper.insert(tAppConfig);
     }
     @Transactional(propagation=Propagation.REQUIRES_NEW)
     public void saveElement(TFourElement tFourElement) throws Exception{
         tFourElementMapper.insert(tFourElement);
         if(1==1){
             throw new RuntimeException();
         }
     }
}
