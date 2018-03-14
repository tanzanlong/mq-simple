package com.baibei.accountservice.settlement.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

@Slf4j
@Configuration
@EnableApolloConfig
public class BalanceSnapShotDynamicConfig {

    @ApolloConfig
    private Config config;
    
    /*@Value("${TEST}")
    @Getter
    @Setter
    private String test;*/
    @Value("${BALANCE.SNAPSHOT.SWITCH}")
    @Getter
    @Setter
    private String balanceSnapShotSwitch;
    
    @ApolloConfigChangeListener
    private void someOnChange(ConfigChangeEvent changeEvent) {
       if (changeEvent.isChanged("switch_system_test")) {
    	   balanceSnapShotSwitch = config.getProperty("switch_system_test","false");
            log.info("Properties entry balanceSnapShotSwitch change to {}", balanceSnapShotSwitch);
        } /**/
    }
    
    
}
