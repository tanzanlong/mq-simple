package com.baibei.accountservice.settlement.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.account.comm.Constants.DailyTaskStatus;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.dao.TDailyTaskMapper;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierOrder;
import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import com.baibei.accountservice.paycenter.common.Constants.DailyTaskType;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.utill.FTPUtil;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.vo.DailySettlementNotifyRequest;
import com.baibei.accountservice.settlement.business.vo.Detail;
import com.baibei.accountservice.settlement.business.vo.SettlementItem;

@Service
public class DailySettlementBusiness {

	static final Logger logger = LoggerFactory.getLogger(DailySettlementBusiness.class);

	static final int pageSize = 500;
	
	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	TDailyTaskMapper tDailyTaskMapper;
	
	@Autowired
	TAccountCashierLogMapper tAccountCashierLogMapper;
	
	@Autowired
	SettlementBusiness settlementBusiness;
	
	//获取组装报文需要的数据
	public void getData4AssemblyMessage(String businessType, Date beginDate, Date endDate){
		try {
			String writeFileName = FTPUtil.generateFileName(PayCenterConstant.DAILY_SETTLEMENT_FILENAME_PREFIX, null, null, businessType,beginDate);
			String ftpHost = dailyTaskConfig.getFtpHost();
            int ftpPort = Integer.parseInt(dailyTaskConfig.getFtpPort());
            String ftpUserName = dailyTaskConfig.getFtpUserName();
            String ftpPassword = dailyTaskConfig.getFtpPassword();
            String writeTempFilePath = dailyTaskConfig.getLocalTempFilePath();
            String remoteFilePath = dailyTaskConfig.getRemoteFilePath();
            String ftpProtocol = dailyTaskConfig.getFtpProtocol();
            
			TDailyTaskExample example = new TDailyTaskExample();
			example.createCriteria().andFileNameEqualTo(writeFileName);
			List<TDailyTask> tDailyTaskList = tDailyTaskMapper.selectByExample(example);
			if(null == tDailyTaskList || 0 == tDailyTaskList.size()){
			    long startTime = System.currentTimeMillis();
                int totalRecord = settlementBusiness.countCashierOrderSizeByTime(beginDate, endDate);
                int totalPage = toPage(totalRecord);
                String filleFileName = writeTempFilePath + "/" + writeFileName;
                File f = new File(filleFileName);
                BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
                for(int currentPage=0; currentPage<totalPage; currentPage++){
                    //分页查出订单
                    List<TAccountCashierOrder> tAccountCashierOrderList = settlementBusiness.qryAccountCashierOrderList(currentPage, pageSize, beginDate, endDate);
                    if(CollectionUtils.isNotEmpty(tAccountCashierOrderList)){
                        //按订单查出费项
                        List<TAccountCashierLog> tAccountCashierLogList = settlementBusiness.qryCashierLogList(tAccountCashierOrderList);
                        if(CollectionUtils.isNotEmpty(tAccountCashierLogList)){
                            StringBuilder sb = format(tAccountCashierLogList, businessType);
                            bw.write(sb.toString());
                            bw.flush();
                        }
                    }
                }
                IOUtils.closeQuietly(bw);
                logger.info("generate daily settlement [{}] file time: {} MS", totalRecord, System.currentTimeMillis() - startTime);
				
				if(0 < totalPage){
					boolean uploadRes = FTPUtil.uploadFileToFTP(ftpHost, ftpPort, ftpUserName, ftpPassword, writeFileName, writeTempFilePath,remoteFilePath,ftpProtocol);
					if(uploadRes){
						TDailyTask record = new TDailyTask ();
						record.setTaskType(DailyTaskType.SETTLEMENT.getValue());
						record.setFileName(writeFileName);
						record.setStatus(DailyTaskStatus.INIT.getValue());
						record.setDealDate(beginDate);
						
						tDailyTaskMapper.insert(record);
						logger.info("日结算对账文件已生成!文件名是:"+writeFileName);
						//调用业务系统接口
						String notifyUrl = dailyTaskConfig.getSettlementCustomerBaseUrl()+dailyTaskConfig.getNotifyUrl();
						DailySettlementNotifyRequest dailySettlementNotifyRequest = new DailySettlementNotifyRequest();
				        Map<String, String> headParams = new HashMap<String, String>();
				        headParams.put("Content-Type", "application/json;charset=UTF-8");
				        dailySettlementNotifyRequest.setFileType(PayCenterConstant.NotifyFileType.SETTLEMENT.getValue());
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
			logger.info("生成日结算对账文件异常"+e);
		}
	}
	
	//将一批数据转成多行文件格式，放入StringBuilder
    private StringBuilder format(List<TAccountCashierLog> cashierLogList, String businessType){
        StringBuilder sb = new StringBuilder();
        Map<String, SettlementItem> orderId2SettlementItem = new LinkedHashMap<String, SettlementItem>();
        for(TAccountCashierLog cashierLog : cashierLogList){
//            String key = cashierLog.getOrderType() + "$" + cashierLog.getOrderId();
            String key = cashierLog.getOrderId();
            SettlementItem settlementItem = orderId2SettlementItem.get(key);
            if(settlementItem == null){
                settlementItem = new SettlementItem();
                settlementItem.setBusinessType(businessType);
                settlementItem.setOrderId(cashierLog.getOrderId());
                settlementItem.setOrderTime(cashierLog.getCreateTime());
                settlementItem.setOrderType(cashierLog.getOrderType());
                settlementItem.setProductCode("");
                orderId2SettlementItem.put(key, settlementItem);
            }
            Detail detail = new Detail();
            detail.setAccountId(cashierLog.getAccountId());
            detail.setAmount(cashierLog.getChangeAmount());
            detail.setFeeType(NumberUtils.toInt(cashierLog.getFeeItem()));
            detail.setUserId(cashierLog.getUserId());
            settlementItem.getItems().add(detail);
        }
        Collection<SettlementItem> settlementItems = orderId2SettlementItem.values();
        for(SettlementItem settlementItem : settlementItems){
            checkSum(settlementItem);
            sb.append(JSON.toJSONString(settlementItem));
            sb.append("\r\n");
        }
        return sb;
    }
    
    private void checkSum(SettlementItem settlement){
//        if(!"CH".equalsIgnoreCase(settlement.getBusinessType())){//新模式则不检查
//            List<Detail> detailList = settlement.getItems();
//            Map<String, String> ignoreOrderTypeMap = new HashMap<String, String>();
//            ignoreOrderTypeMap.put(Constants.ORDER_TYPE_RECHARGE, Constants.ORDER_TYPE_RECHARGE);
//            ignoreOrderTypeMap.put(Constants.ORDER_TYPE_RECHARGE_ROLLBACK, Constants.ORDER_TYPE_RECHARGE_ROLLBACK);
//            ignoreOrderTypeMap.put(Constants.ORDER_TYPE_WITHDRAW, Constants.ORDER_TYPE_WITHDRAW);
//            ignoreOrderTypeMap.put(Constants.ORDER_TYPE_WITHDRAW_ROLLBACK, Constants.ORDER_TYPE_WITHDRAW_ROLLBACK);
//            if(!ignoreOrderTypeMap.containsKey(settlement.getOrderType())){
//                long sum = 0;
//                if(CollectionUtils.isNotEmpty(detailList)){
//                    for(Detail detail : detailList){
//                        sum += detail.getAmount();
//                    }
//                }
//                if(sum != 0){
//                    throw new IllegalArgumentException("订单[" + settlement.getOrderId() + "]未通过零和检验");
//                }
//            }
//        }
    }
    
	public int toPage(int totalRecord){
        if (totalRecord % pageSize != 0){
            return totalRecord / pageSize + 1;
        }else{
            return totalRecord /pageSize;
        }
    }
}
