package com.baibei.accountservice.redisSub;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.config.MarketGoodInfoConfig;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.settlement.business.DailyBalanceBusiness;
import com.baibei.accountservice.settlement.business.DailySettlementBusiness;
import com.baibei.accountservice.settlement.business.DailySettlementTicketBusiness;
import com.baibei.accountservice.util.JedisClient;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component
@Slf4j
public class AutoCloseSuccessListener extends JedisPubSub  implements Runnable{

	static final Logger logger = LoggerFactory.getLogger(AutoCloseSuccessListener.class);

	@Autowired
	MarketGoodInfoConfig marketGoodInfoConfig;
	
	@Autowired
	JedisClient jedisClient;
	
	@Autowired
    DailySettlementBusiness dailySettlementBusiness;
	
	@Autowired
    DailyBalanceBusiness dailyBalanceBusiness;
	
	@Autowired
    DailySettlementTicketBusiness dailySettlementTicketBusiness;

    @Autowired
    SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;

	Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


	@Override
	public void run() {
		String[] channelArray = new String[]{"SCHEDULER:CHANNEL:CLOSE_ORDER_NOTICE"};
		while(true){
			try{
				Jedis redis = this.jedisClient.getJedis();
				try{
					jedisClient.subscribeMsg(this, channelArray);
				}catch(JedisConnectionException e){
					logger.warn("Exception :", e);
					logger.warn("Exit redis psubscribe, retry after 1 second");
				}catch(Exception e){
					logger.error("Exception :", e);
				}
				try{
					Thread.sleep(1000);
				}catch(Exception unused){
				}
				try{
					if(redis != null){
						redis.close();
					}
				}catch(Exception unused){
				}
			}catch(Exception e){
				logger.error("Final Exception {}", e);
			}
		}
	}

	// 取得订阅的消息后的处理
	public void onMessage(String channel, String message) {
		try {
			log.info("receive message {} from channel {}", message, channel);
			if (schedulerMasterCheck.isMaster()) {
				for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
					String exchangeTag = entry.getKey();
					DateSourceLocal.setExchangeTag(exchangeTag);
					doOnMessage(channel, message);
				}
            }
		} catch (Throwable t) {
			log.error("", t);
		}
	}

	private void doOnMessage(String channel, String message) {
		JSONObject jsonObj = JSONObject.parseObject(message);
		log.info("execute dailySettlement");
		Date beginDate = jsonObj.getDate("tradeDayStartTime");
		Date endDate = jsonObj.getDate("closeOrderFinishTime");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.info("beginDate = {}", df.format(beginDate));
		log.info("endDate = {}", df.format(endDate));
		dailySettlementBusiness.getData4AssemblyMessage(dailyTaskConfig.getPaycenterAppId(), beginDate, endDate);
		dailyBalanceBusiness.getData4AssemblyMessage(dailyTaskConfig.getPaycenterAppId(), beginDate, endDate);
		dailySettlementTicketBusiness.getData4AssemblyMessage(dailyTaskConfig.getPaycenterAppId(), beginDate, endDate);
	}

}