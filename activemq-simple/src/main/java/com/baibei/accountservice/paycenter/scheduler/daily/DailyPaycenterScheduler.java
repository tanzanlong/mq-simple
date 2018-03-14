package com.baibei.accountservice.paycenter.scheduler.daily;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.paycenter.bussiness.DailyPaycenterBusiness;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;

import java.util.Map;

/**
 * 定时生成出入金对账文件
 * 
 * @author lich
 */
@Component
@EnableScheduling
public class DailyPaycenterScheduler {

	static final Logger logger = LoggerFactory.getLogger(DailyPaycenterScheduler.class);

	@Autowired
	DailyPaycenterBusiness dailyPaycenterBusiness;

	@Autowired
    SchedulerMasterCheck schedulerMasterCheck;
	
	@Autowired
	DailyTaskConfig dailyTaskConfig;

	Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


	// 每隔15分钟，扫描一次
	@Scheduled(cron = "0 */5 * * * ?")
	public void generateTaskData() {
		if (schedulerMasterCheck.isMaster()) {

			for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
				String exchangeTag = entry.getKey();
				DateSourceLocal.setExchangeTag(exchangeTag);
				//
				dailyPaycenterBusiness.getData4AssemblyMessage(dailyTaskConfig.getPaycenterAppId());
			}

		}
	}
}
