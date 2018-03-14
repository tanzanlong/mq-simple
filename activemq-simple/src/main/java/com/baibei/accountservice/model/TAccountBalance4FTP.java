package com.baibei.accountservice.model;

import java.io.Serializable;

public class TAccountBalance4FTP implements Serializable {

	private static final long serialVersionUID = -9091108565888359937L;

	private Long accountId;

	private Long balance;

	private String businessType = "CH";

	private String userId;

	private Boolean avaliableFlag = false;

	private Boolean freezonFlag = false;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getAvaliableFlag() {
		return avaliableFlag;
	}

	public void setAvaliableFlag(Boolean avaliableFlag) {
		this.avaliableFlag = avaliableFlag;
	}

	public Boolean getFreezonFlag() {
		return freezonFlag;
	}

	public void setFreezonFlag(Boolean freezonFlag) {
		this.freezonFlag = freezonFlag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}