package com.baibei.accountservice.paycenter.bussiness;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.dao.TAppConfigMapper;
import com.baibei.accountservice.model.TAppConfig;
import com.baibei.accountservice.model.TAppConfigExample;

/**
 * 出入金app_id和app_secret配置服务
 * @author peng
 */
@Service
public class AppConfigBusiness {

    @Autowired
    TAppConfigMapper tAppConfigMapper;
    
    public TAppConfig qryTAppConfigByAppId(String appId){
        TAppConfigExample example =new TAppConfigExample();
        example.createCriteria().andAppIdEqualTo(appId).andStatusEqualTo(1);
        List<TAppConfig> list = tAppConfigMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }
    
    public List<TAppConfig> queryAll(){
        TAppConfigExample example =new TAppConfigExample();
        example.createCriteria().andStatusEqualTo(1);
        List<TAppConfig> list = tAppConfigMapper.selectByExample(example);
        return list;
    }
}
