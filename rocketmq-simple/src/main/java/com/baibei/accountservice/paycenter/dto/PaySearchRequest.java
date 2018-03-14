package com.baibei.accountservice.paycenter.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
@Data
public class PaySearchRequest {
	List<String> userIds;
	Date startTime;
	Date endTime;
	String type;
	String status;
	int pageSize;
	int pageNum;

}
