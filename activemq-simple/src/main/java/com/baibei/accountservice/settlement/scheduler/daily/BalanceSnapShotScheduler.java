package com.baibei.accountservice.settlement.scheduler.daily;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.settlement.business.BalanceSnapShotBusiness;
import com.baibei.accountservice.settlement.config.BalanceSnapShotDynamicConfig;

import java.util.Map;

/**
 * 把余额表中的数据搬到BalanceSnapShot表,方便测试,生产时需要关掉
 * 一分钟执行一次
 * 
 * @author lich
 */
@Component
@EnableScheduling
public class BalanceSnapShotScheduler {

	static final Logger logger = LoggerFactory.getLogger(BalanceSnapShotScheduler.class);

	@Autowired
	BalanceSnapShotDynamicConfig balanceSnapShotDynamicConfig;
	
	@Autowired
	BalanceSnapShotBusiness balanceSnapShotBusiness;

	@Autowired
    SchedulerMasterCheck schedulerMasterCheck;

	Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


	// 每隔5分钟，扫描一次
//	@Scheduled(cron = "0 */10 * * * ?")
//	@Scheduled(cron = "0/15 * * * * ?")
	public void generateTaskData() {
		if (schedulerMasterCheck.isMaster()) {


			for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
				String exchangeTag = entry.getKey();
				DateSourceLocal.setExchangeTag(exchangeTag);
				String balanceSnapShotSwitch = balanceSnapShotDynamicConfig.getBalanceSnapShotSwitch();
				if("true".equals(balanceSnapShotSwitch)){

					balanceSnapShotBusiness.generateBalanceSnapShotData();
				}
			}
//			if (true) {

			
		}
	}
}
