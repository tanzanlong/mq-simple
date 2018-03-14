package com.baibei.accountservice.paycenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableApolloConfig
public class DailyTaskConfig {

	@Value("${FTP.DAILY.BALANCE.HOST}")
	@Getter
	@Setter
	private String ftpHost;

	@Value("${FTP.DAILY.BALANCE.PORT}")
	@Getter
	@Setter
	private String ftpPort;

	@Value("${FTP.DAILY.BALANCE.USERNAME}")
	@Getter
	@Setter
	private String ftpUserName;

	@Value("${FTP.DAILY.BALANCE.PASSWORD}")
	@Getter
	@Setter
	private String ftpPassword;
	
	@Value("${FTP.DAILY.LOCAL.TEMPFILE.PATH}")
	@Getter
	@Setter
	private String localTempFilePath;

	@Value("${FTP.DAILY.PROTOCOL}")
	@Getter
	@Setter
	private String ftpProtocol;
	
	@Value("${FTP.DAILY.REMOTE.FILE.PATH}")
	@Getter
	@Setter
	private String remoteFilePath;

	@Value("${DAILY.TASK.NOTIFY.URL}")
	@Getter
	@Setter
	private String notifyUrl;
	
	@Value("${DAILY.TASK.APP.SECRET}")
	@Getter
	@Setter
	private String appSecret;

	@Value("${SETTLEMENT.PAYCENTER.BASEURL}")
	@Getter
	@Setter
	private String settlementPaycenterBaseUrl;
	
	@Value("${SETTLEMENT.CUSTOMER.BASEURL}")
	@Getter
	@Setter
	private String settlementCustomerBaseUrl;
	
	@Value("${PAYCENTER.BASEURL}")
    @Getter
    @Setter
    private String paycenterBaseUrl;
	
	@Value("${PAYCENTER.APPID}")
    @Getter
    @Setter
    private String paycenterAppId;
	
	@Value("${PAYCENTER.APPKEY}")
    @Getter
    @Setter
    private String paycenterAppKey;
	
	
}
