package com.baibei.accountservice.paycenter.bussiness;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.dao.TDailyTaskMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.common.Constants.DailyTaskType;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.utill.FTPUtil;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.vo.DailySettlementNotifyRequest;

import lombok.Data;

/**
 * 出入金对账
 * @author peng
 */
@Service
public class DailyPaycenterBusiness {

	static final Logger logger = LoggerFactory.getLogger(DailyPaycenterBusiness.class);

	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	TDailyTaskMapper tDailyTaskMapper;
	
	@Autowired
	TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;

	//获取组装报文需要的数据
	public void getData4AssemblyMessage(String businessType){
		try {
			
			Date nowDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(nowDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			
			Date endDate = c.getTime();
			c.add(Calendar.DAY_OF_MONTH, -1);
			Date beginDate = c.getTime();
			String writeFileName = FTPUtil.generateFileName(PayCenterConstant.DAILY_PAYCENTER_FILENAME_PREFIX, PayCenterConstant.DAILY_REQ_FLAG, null, businessType,beginDate);
			
			TDailyTaskExample example = new TDailyTaskExample();
			example.createCriteria().andFileNameEqualTo(writeFileName);
			List<TDailyTask> tDailyTaskList = tDailyTaskMapper.selectByExample(example);
			if(null == tDailyTaskList || 0 == tDailyTaskList.size()){
			    Long startTime = System.currentTimeMillis();
			    List<TRechargeWithdrawOrder> inoutOrderList = qryInoutOrderByDate(beginDate, endDate);
	            if(CollectionUtils.isNotEmpty(inoutOrderList)){
					String ftpHost = dailyTaskConfig.getFtpHost();
					int ftpPort = Integer.parseInt(dailyTaskConfig.getFtpPort());
					String ftpUserName = dailyTaskConfig.getFtpUserName();
					String ftpPassword = dailyTaskConfig.getFtpPassword();
					String writeTempFilePath = dailyTaskConfig.getLocalTempFilePath();
					String remoteFilePath = dailyTaskConfig.getRemoteFilePath();
					String ftpProtocol = dailyTaskConfig.getFtpProtocol();
					
					StringBuilder sb = format(inoutOrderList);
					logger.info("generate daily paycenter [{}] file time: {} MS", inoutOrderList.size(), System.currentTimeMillis() - startTime);
					
					boolean uploadRes = FTPUtil.writeFileToFTP(ftpHost, ftpPort, ftpUserName, ftpPassword, writeFileName, sb.toString(), writeTempFilePath,remoteFilePath,ftpProtocol);
					
					if(uploadRes){
						TDailyTask record = new TDailyTask ();
						record.setTaskType(DailyTaskType.PAYCENTER.getValue());
						record.setFileName(writeFileName);
						record.setStatus("INIT");
						record.setDealDate(beginDate);
						
						tDailyTaskMapper.insert(record);
						logger.info("出入金对账文件已生成!文件名是:"+writeFileName);
						//调用业务系统接口
						String notifyUrl = dailyTaskConfig.getSettlementPaycenterBaseUrl()+dailyTaskConfig.getNotifyUrl();
						DailySettlementNotifyRequest dailySettlementNotifyRequest = new DailySettlementNotifyRequest();
						Map<String, String> headParams = new HashMap<String, String>();
						headParams.put("Content-Type", "application/json;charset=UTF-8");
						dailySettlementNotifyRequest.setFileType(PayCenterConstant.NotifyFileType.PAYCENTER.getValue());
						dailySettlementNotifyRequest.setFileName(writeFileName);
						dailySettlementNotifyRequest.setBusinessType(businessType);
						String message = JSON.toJSONString(dailySettlementNotifyRequest);
						try {
							logger.info("post {} to url {}", message, notifyUrl);
							HttpClientUtils.doPost(notifyUrl, headParams, message);
						} catch (Exception e) {
							logger.error("通知清结算系统异常"+e.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			logger.info("生成出入金对账文件异常"+e);
		}
	}
	
	public List<TRechargeWithdrawOrder> qryInoutOrderByDate(Date startTime, Date endTime){
	    TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
	    example.createCriteria().andCreateTimeBetween(startTime, endTime);
	    return tRechargeWithdrawOrderMapper.selectByExample(example);
	}
	
	private StringBuilder format(List<TRechargeWithdrawOrder> orderList){
	    StringBuilder sb = new StringBuilder();
	    for(TRechargeWithdrawOrder order : orderList){
	        Item item = new Item();
	        item.setBusinessType(order.getBusinessType());
	        item.setAmount(order.getAmount());
	        item.setOrderId(order.getOrderId());
	        item.setOrderStatus(order.getStatus());
	        item.setOrderType(order.getOrderType());
            sb.append(JSON.toJSONString(item));
            sb.append("\r\n"); 
        }
        return sb;
    }
	 
	@Data
	public static class Item{
	    private String businessType;
	    private String orderId;
	    private Long amount;
	    private String orderType;
	    private String orderStatus;
	}

}
