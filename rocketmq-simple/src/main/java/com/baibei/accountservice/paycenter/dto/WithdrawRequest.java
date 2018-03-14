package com.baibei.accountservice.paycenter.dto;

import lombok.Data;

@Data
public class WithdrawRequest {
	/**
	 * 流水号，生成规则：服务调用方标识（2位字母）+时间戳后10位+6位随机数
	 */
	private String serialNo;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 入金金额（分）
	 */
	private Long amount;
	/**
	 * 资金密码
	 */
	private String password;
}
