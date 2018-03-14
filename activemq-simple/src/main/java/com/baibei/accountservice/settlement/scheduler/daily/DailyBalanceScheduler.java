package com.baibei.accountservice.settlement.scheduler.daily;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.settlement.business.DailyBalanceBusiness;

/**
 * 定时生成余额对账文件
 * 
 * @author lich
 */
@Component
@EnableScheduling
public class DailyBalanceScheduler {

	static final Logger logger = LoggerFactory.getLogger(DailyBalanceScheduler.class);

	@Autowired
	DailyBalanceBusiness dailyBalanceBusiness;

	@Autowired
    SchedulerMasterCheck schedulerMasterCheck;
	
	@Autowired
	DailyTaskConfig dailyTaskConfig;

	// 每隔5分钟，扫描一次
	//@Scheduled(cron = "0 */5 * * * ?")
	public void generateTaskData() {
		if (schedulerMasterCheck.isMaster()) {
		    //dailyBalanceBusiness.getData4AssemblyMessage(dailyTaskConfig.getPaycenterAppId());
		}
	}
}
