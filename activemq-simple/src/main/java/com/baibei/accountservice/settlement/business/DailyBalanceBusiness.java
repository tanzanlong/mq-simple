package com.baibei.accountservice.settlement.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.account.comm.Constants.DailyTaskStatus;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TDailyTaskMapper;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant.DailyTaskType;
import com.baibei.accountservice.paycenter.utill.FTPUtil;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.vo.DailySettlementNotifyRequest;
import com.baibei.accountservice.settlement.business.executor.DailyBalanceProducer.DailyBalanceItem;

@Service
public class DailyBalanceBusiness {

	static final Logger logger = LoggerFactory.getLogger(DailyBalanceBusiness.class);
	
	static final int pageSize = 500;

	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	TDailyTaskMapper tDailyTaskMapper;
	
	@Autowired
	TAccountBalanceMapper tAccountBalanceMapper;
	
	@Autowired
	TAccountMapper tAccountMapper;
	
	@Autowired
	SettlementBusiness settlementBusiness;
	
	//获取组装报文需要的数据
	public void getData4AssemblyMessage(String businessType, Date beginDate, Date endDate){
		try {
			List<String> balanceTypeList = new ArrayList<String>();
			balanceTypeList.add("AVALIABLE");
			balanceTypeList.add("FREEZON");
			
			String writeFileName = FTPUtil.generateFileName(PayCenterConstant.DAILY_BALANCE_FILENAME_PREFIX, PayCenterConstant.DAILY_REQ_FLAG, null, businessType,beginDate);
			
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
			    int totalRecord = settlementBusiness.countAccountSize();
			    int totalPage = toPage(totalRecord);
			    String filleFileName = writeTempFilePath + "/" + writeFileName;
			    File f = new File(filleFileName);
		        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			    for(int currentPage=0; currentPage<totalPage; currentPage++){
			        List<Long> accountIdList = settlementBusiness.qryAccountIdList(currentPage, pageSize);
			        if(CollectionUtils.isNotEmpty(accountIdList)){
			            List<TAccountBalance> accountBalanceList = settlementBusiness.qryAccountBalanceList(accountIdList);
			            if(CollectionUtils.isNotEmpty(accountBalanceList)){
			                StringBuilder sb = format(accountBalanceList, businessType);
			                bw.write(sb.toString());
			                bw.flush();
			            }
			        }
			    }
			    IOUtils.closeQuietly(bw);
			    logger.info("generate daily balance [{}] file time: {} MS", totalRecord, System.currentTimeMillis() - startTime);
				if(0 < totalPage){
					boolean uploadRes = FTPUtil.uploadFileToFTP(ftpHost, ftpPort, ftpUserName, ftpPassword, writeFileName, writeTempFilePath,remoteFilePath,ftpProtocol);
					if(uploadRes){
						TDailyTask record = new TDailyTask ();
						record.setTaskType(DailyTaskType.BALANCE.getValue());
						record.setFileName(writeFileName);
						record.setStatus(DailyTaskStatus.INIT.getValue());
						record.setDealDate(beginDate);
						
						tDailyTaskMapper.insert(record);
						logger.info("余额对账文件已生成!文件名是:"+writeFileName);
						//调用业务系统接口
						String notifyUrl = dailyTaskConfig.getSettlementCustomerBaseUrl()+dailyTaskConfig.getNotifyUrl();
						DailySettlementNotifyRequest dailySettlementNotifyRequest = new DailySettlementNotifyRequest();
						Map<String, String> headParams = new HashMap<String, String>();
						headParams.put("Content-Type", "application/json;charset=UTF-8");
						dailySettlementNotifyRequest.setFileType(PayCenterConstant.NotifyFileType.BALANCE.getValue());
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
			e.printStackTrace();
			logger.info("生成余额对账文件异常"+e);
		}
	}
	
	//将一批数据转成多行文件格式，放入StringBuilder
    private StringBuilder format(List<TAccountBalance> accountBalanceList, String businessType){
        Map<Long, DailyBalanceItem> accountId2BalanceItem = new HashMap<Long, DailyBalanceItem>();
        for(TAccountBalance tAccountBalance : accountBalanceList){
            DailyBalanceItem oldItem = accountId2BalanceItem.get(tAccountBalance.getAccountId());
            if(oldItem == null){
                DailyBalanceItem item = new DailyBalanceItem();
                item.setBusinessType(businessType);
                item.setAccountId(tAccountBalance.getAccountId());
                item.setUserId(tAccountBalance.getUserId());
                item.setBalance(tAccountBalance.getAmount());
                accountId2BalanceItem.put(tAccountBalance.getAccountId(), item);
            }else{
                oldItem.setBalance(oldItem.getBalance() + tAccountBalance.getAmount());
            }
        }
        Collection<DailyBalanceItem> dailyBalanceItems = accountId2BalanceItem.values();
        StringBuilder sb = new StringBuilder();
        for(DailyBalanceItem item : dailyBalanceItems){
            sb.append(JSON.toJSONString(item));
            sb.append("\r\n");
        }
        return sb;
    }
    
	public int toPage(int totalRecord){
        if (totalRecord % pageSize != 0){
            return totalRecord / pageSize + 1;
        }else{
            return totalRecord /pageSize;
        }
    }
}
