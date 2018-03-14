package com.baibei.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableApolloConfig
public class SchedulerMasterConfig {
	
	@Value("${SCHEDULER.MASTER.ZK}")
    @Getter
    @Setter
    private String zk;
    
	
	@Value("${SCHEDULER.MASTER.ZKSESSIONTIMEOUT}")
    @Getter
    @Setter
    private int zkSessionTimeout;
	
	/*public String getTestString(){
	    return this.dynamicConfig().getTest();
	}*/
	
	@Bean
	public DynamicConfig dynamicConfig() {
	    return new DynamicConfig();
	}
}
