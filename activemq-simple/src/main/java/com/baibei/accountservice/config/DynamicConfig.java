package com.baibei.accountservice.config;

import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableApolloConfig
public class DynamicConfig {

    @ApolloConfig
    private Config config;
    
    public String getRocketMqNameAddr(){
        return config.getProperty("ROCKETMQ.NAMEADDR", "");
    }
    
    public String getSwitchSystemTest(){
        return config.getProperty("getSwitchSystemTest", "false");
    }
    
    @ApolloConfigChangeListener
    private void someOnChange(ConfigChangeEvent changeEvent) {
       if (changeEvent.isChanged("switch_system_test")) {
            log.info("Properties entry switch_system_test change to {}");
        } /**/
    }
    
    
}
