package com.baibei.accountservice.redisSub;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.comm.RedisConstants;
import com.baibei.accountservice.config.MarketGoodInfoConfig;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.util.JedisClient;
import com.baibei.accountservice.vo.ch.GoodsInformation;
import com.baibei.accountservice.vo.ch.MarketInformation;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component
public class MarketAndGoodInfoListener extends JedisPubSub  implements Runnable{

	static final Logger logger = LoggerFactory.getLogger(MarketAndGoodInfoListener.class);

	@Autowired
	MarketGoodInfoConfig marketGoodInfoConfig;
	
	@Autowired
	JedisClient jedisClient;
	
	@Override
	public void run() {
		String[] channelArray = new String[]{RedisConstants.MARKET_OPENCLOSE_STATUS_PUBLISH,RedisConstants.PRODUCT_INFO_PUBLISH};
		
		while(true){
			try{
				Jedis redis = this.jedisClient.getJedis();
				try{
//					redis.psubscribe(this, channelArray);
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
		//市场开休市
		if(RedisConstants.MARKET_OPENCLOSE_STATUS_PUBLISH.equals(channel)){
//			message = "{'marketCode':'lich','updateTime':'1497583721383','status':'open'}";
			JSONObject jsonObj = JSON.parseObject(message);

			String marketCodeStr = (String) jsonObj.get("marketCode");
			String statusStr = (String) jsonObj.get("status");

			MarketInformation marketInformation = new MarketInformation();

			marketInformation.setMarketNo(marketCodeStr);
			marketInformation.setMarketStatus(statusStr);

			// 调用业务系统接口
			String customerMarketInfoUrl = marketGoodInfoConfig.getSettlementCustomerBaseUrl() + marketGoodInfoConfig.getCustomerMarketInfoUrl();
			Map<String, String> headParams = new HashMap<String, String>();
			headParams.put("Content-Type", "application/json;charset=UTF-8");

			String marketInformationJson = JSON.toJSONString(marketInformation);
			try {
				logger.info("post {} to url {}", marketInformationJson, customerMarketInfoUrl);
				String res = HttpClientUtils.doPost(customerMarketInfoUrl, headParams, marketInformationJson);
				logger.info("清结算返回的结果为 {}", res);
			} catch (Exception e) {
				logger.error("通知业务系统异常" + e.getMessage());
			}
		}else{
		//商品信息
			// TODO 这里的message暂时先自己定义一个值
//			message = "{'type' : 'add',data:{'productCode':'001','productName':'测试商品','exchange':'广东贵金属交易所','market':'现货市场'}}";
			message = "{'type' : 'update',data:{'productCode':'001','productName':'测试商品嘎嘎','exchange':'广东贵金属交易所','market':'现货市场'}}";
			JSONObject jsonObj = JSON.parseObject(message);

			String typeStr = (String) jsonObj.get("type");
			JSONObject dataObj = (JSONObject) jsonObj.get("data");

			GoodsInformation goodsInformation = new GoodsInformation();

			goodsInformation.setType(typeStr);
			goodsInformation.setGoodsNo((String) dataObj.get("productCode"));
			goodsInformation.setGoodsName((String) dataObj.get("productName"));
			goodsInformation.setCenterNo((String) dataObj.get("exchange"));
			goodsInformation.setMarketNo((String) dataObj.get("market"));

			// 调用业务系统接口
			String customerGoodInfoUrl =marketGoodInfoConfig.getSettlementCustomerBaseUrl() + marketGoodInfoConfig.getCustomerGoodInfoUrl();
			Map<String, String> headParams = new HashMap<String, String>();
			headParams.put("Content-Type", "application/json;charset=UTF-8");

			String marketInformationJson = JSON.toJSONString(goodsInformation);
			try {
				logger.info("post {} to url {}", marketInformationJson, customerGoodInfoUrl);
				String res = HttpClientUtils.doPost(customerGoodInfoUrl, headParams, marketInformationJson);
				logger.info("清结算返回的结果为 {}", res);
			} catch (Exception e) {
				logger.error("通知业务系统异常" + e.getMessage());
			}
		}
	}
}