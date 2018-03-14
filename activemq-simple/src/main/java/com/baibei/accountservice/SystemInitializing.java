package com.baibei.accountservice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.config.DynamicConfig;
import com.baibei.accountservice.redisSub.AutoCloseSuccessListener;
import com.baibei.accountservice.redisSub.MarketAndGoodInfoListener;
import com.baibei.accountservice.rocketmq.RocketMQUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SystemInitializing implements InitializingBean {

	static Logger logger = LoggerFactory.getLogger(SystemInitializing.class);
	
    @Autowired 
    SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    RocketMQUtils rocketMQUtils;
    
    @Autowired
	MarketAndGoodInfoListener marketAndGoodInfoListener;
    
    @Autowired
    AutoCloseSuccessListener autoCloseSuccessListener;
    
    @Autowired
    DynamicConfig dynamicConfig;
    
    private ExecutorService executorService;
    
    public void afterPropertiesSet() throws Exception {
        doInit();
    }

    private void doInit() {
        try {
        	executorService = Executors.newFixedThreadPool(2);
            schedulerMasterCheck.init();
            logger.info("Start schedulerMasterCheck ...");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("error:{}", e.getMessage());
        }
        
        if(StringUtils.isNotBlank(dynamicConfig.getRocketMqNameAddr())){
            try{
                rocketMQUtils.init();
                logger.info("Start rocketMQUtils ...");
            }catch(Exception e){
                e.printStackTrace();
                log.info("error:{}", e.getMessage());
            }
        }
        
//        executorService.execute(marketAndGoodInfoListener);
//        logger.info("Start marketAndGoodInfoListenerThread ...");
//
        executorService.execute(autoCloseSuccessListener);
        logger.info("Start autoCloseSuccessListener ...");
    }

}
