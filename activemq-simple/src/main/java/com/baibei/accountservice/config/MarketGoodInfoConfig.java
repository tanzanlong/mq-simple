package com.baibei.accountservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableApolloConfig
public class MarketGoodInfoConfig {
	
	@Value("${SETTLEMENT.CUSTOMER.BASEURL}")
	@Getter
	@Setter
	private String settlementCustomerBaseUrl;

	@Value("${CUSTOMER.MARKETINFO.URL}")
	@Getter
	@Setter
	private String customerMarketInfoUrl;
	@Value("${CUSTOMER.GOODINFO.URL}")
	@Getter
	@Setter
	private String customerGoodInfoUrl;

	/*public String getTestString() {
		return this.dynamicConfig().getTest();
	}*/

	@Bean
	public DynamicConfig dynamicConfig() {
		return new DynamicConfig();
	}
}
