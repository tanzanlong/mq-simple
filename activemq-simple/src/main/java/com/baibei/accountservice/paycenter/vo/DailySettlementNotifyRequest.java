package com.baibei.accountservice.paycenter.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 日结清算触发请求
 * 
 * @author lich
 * 
 */
@ToString
public class DailySettlementNotifyRequest {

	@Setter
	@Getter
	private String fileType;
	
	@Setter
	@Getter
	private String fileName;

	@Setter
	@Getter
	private String businessType;
}
