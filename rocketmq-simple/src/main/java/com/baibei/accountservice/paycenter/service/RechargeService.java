package com.baibei.accountservice.paycenter.service;

import com.baibei.accountservice.paycenter.dto.RechargeRequest;

public interface RechargeService {
	/**
	 * 充值请求
	 * 
	 * @param req
	 */
	public void rechargeRequest(RechargeRequest req);
}
