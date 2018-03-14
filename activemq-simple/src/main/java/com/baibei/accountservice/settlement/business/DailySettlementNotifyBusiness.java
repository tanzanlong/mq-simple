package com.baibei.accountservice.settlement.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceSnapshotMapper;
import com.baibei.accountservice.dao.TDailyPaycenterResultMapper;
import com.baibei.accountservice.dao.TDailySettlementResMapper;
import com.baibei.accountservice.dao.TDailyTaskMapper;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountBalanceSnapshot;
import com.baibei.accountservice.model.TDailyPaycenterResult;
import com.baibei.accountservice.model.TDailyPaycenterResultExample;
import com.baibei.accountservice.model.TDailySettlementRes;
import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import com.baibei.accountservice.paycenter.common.Constants;
import com.baibei.accountservice.paycenter.common.Constants.DailyTaskStatus;
import com.baibei.accountservice.paycenter.common.Constants.DailyTaskType;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.utill.FTPUtil;
import com.baibei.accountservice.rocketmq.RocketMQUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class DailySettlementNotifyBusiness {

	static final Logger logger = LoggerFactory.getLogger(DailySettlementBusiness.class);
	
	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	TDailyTaskMapper tDailyTaskMapper;
	
	@Autowired
	TDailyPaycenterResultMapper tDailyPaycenterResultMapper;
	
	@Autowired
	TAccountBalanceMapper tAccountBalanceMapper;
	
	@Autowired
	TAccountBalanceSnapshotMapper tAccountBalanceSnapshotMapper;
	
	@Autowired
	TDailySettlementResMapper tDailySettlementResMapper;
	
	@Autowired
	RocketMQUtils rocketMQUtils;
	
	
	public void dealSettlementRes(String fileName,String fileType) throws Exception{
		String reqFlag = "";
		if(!PayCenterConstant.NotifyFileType.SETTLEMENT.getValue().equals(fileType)){
			reqFlag = "REQ_";
		}
		String taskType = null;
		//根据fileType改成任务类型
		if(PayCenterConstant.NotifyFileType.BALANCE.getValue().equals(fileType)){
			taskType = DailyTaskType.BALANCE.getValue();
		}else if(PayCenterConstant.NotifyFileType.PAYCENTER.getValue().equals(fileType)){
			taskType = DailyTaskType.PAYCENTER.getValue();
		}else if(PayCenterConstant.NotifyFileType.SETTLEMENT.getValue().equals(fileType)){
			taskType = DailyTaskType.SETTLEMENT.getValue();
		}else{
			throw new IllegalArgumentException("parameter fileType error");
		}
		
		String reqFileName = fileName.replace("RESULT_", reqFlag);
		TDailyTaskExample example = new TDailyTaskExample();
		example.createCriteria().andFileNameEqualTo(reqFileName).andStatusEqualTo(DailyTaskStatus.INIT.getValue()).andTaskTypeEqualTo(taskType);
		List<TDailyTask> tDailyTaskList = tDailyTaskMapper.selectByExample(example);
		if(null != tDailyTaskList && 0 != tDailyTaskList.size()){
			String ftpHost = dailyTaskConfig.getFtpHost();
			int ftpPort = Integer.parseInt(dailyTaskConfig.getFtpPort());
			String ftpUserName = dailyTaskConfig.getFtpUserName();
			String ftpPassword = dailyTaskConfig.getFtpPassword();
			String localTempFilePath = dailyTaskConfig.getLocalTempFilePath();
			String remoteFilePath = dailyTaskConfig.getRemoteFilePath();
			String ftpProtocol = dailyTaskConfig.getFtpProtocol();
			
			TDailyTask tDailyTask = tDailyTaskList.get(0);
				
			File file =  FTPUtil.getFTPFile(ftpHost, ftpPort, ftpUserName, ftpPassword, fileName,localTempFilePath,remoteFilePath,ftpProtocol,false);
			insertData(tDailyTask, file,fileName,fileType);
			if(PayCenterConstant.NotifyFileType.SETTLEMENT.getValue().equals(fileType)){
				logger.info("读取清算结果文件,放入MQ成功,文件名称是:"+fileName);
			}else{
				logger.info("读取清算结果文件,入库成功,文件名称是:"+fileName);
			}
		}else{
			logger.info("任务记录不匹配.清结算返回结果文件名为:"+fileName);
			throw new IllegalArgumentException("no task record");
		}
	}
	
	public void insertData(TDailyTask tDailyTask, File file,String fileName,String fileType) throws Exception {
		//先删除,再插入
		FileInputStream fis = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		
		if(PayCenterConstant.NotifyFileType.SETTLEMENT.getValue().equals(fileType)){
			Boolean settleResult = false;
			String settlementRes = "FAIL";
			
			try {
				if(null != file){
					fis = new FileInputStream(file);
					isr = new InputStreamReader(fis);
					br = new BufferedReader(isr);
					String str = "";
					
					//这里取第一行,解析fail的值,
					
					if ((str = br.readLine()) != null) {
						// 这里是去掉文件头里自带编码,不能去掉
						if (str != null) {
							str = str.replaceAll("\ufeff", "");
						}
						JSONObject jsonObj = JSON.parseObject(str);
						
						Integer failCount = (Integer) jsonObj.get("fail");
						if(0 == failCount){
							settleResult = true;
							settlementRes = "SUCCESS";
						}
						
						SimpleDateFormat simpleDateFormat4MQ = new SimpleDateFormat("yyyy-MM-dd");
						
						String dealDateStr4MQ = simpleDateFormat4MQ.format(tDailyTask.getDealDate());
						
						JSONObject message = new JSONObject();
						message.put("settleDate", dealDateStr4MQ);
						message.put("settleResult", settleResult);
						
						rocketMQUtils.send(Constants.SETTLEMENT_DONE_TOPIC, message.toJSONString());
						
						TDailySettlementRes tDailySettlementRes = new TDailySettlementRes();
						
						tDailySettlementRes.setSettlementDate(tDailyTask.getDealDate());
						
						tDailySettlementRes.setSettlementRes(settlementRes);
						
						tDailySettlementResMapper.insert(tDailySettlementRes);
						
						tDailyTask.setStatus(DailyTaskStatus.DEAL_DONE.getValue());
						tDailyTask.setUpdateTime(new Date());
						
						tDailyTaskMapper.updateByPrimaryKeySelective(tDailyTask);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}finally{
				if(null != br){
					br.close();
				}
				if(null != isr){
					isr.close();
				}
				if(null != fis){
					fis.close();
				}
				if(null != file){
					file.delete();
				}
			}
		}else if(PayCenterConstant.NotifyFileType.BALANCE.getValue().equals(fileType)){
			try {
				if(null != file){

					fis = new FileInputStream(file);
					isr = new InputStreamReader(fis);
					br = new BufferedReader(isr);
					String str = "";
					int count = 0;
					List<Long> accountIdList = new ArrayList<Long>();
					while ((str = br.readLine()) != null) {
						try {
							// 这里是去掉文件头里自带编码,不能去掉
							if (str != null) {
								str = str.replaceAll("\ufeff", "");
							}
							
							//第一行不读取
							if (1 > count++) {
								continue;
							}
							
							JSONObject jsonObj = JSON.parseObject(str);
							Long accountId = Long.parseLong((String) jsonObj.get("accountId"));
							accountIdList.add(accountId);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("解析异常: " + str);
						}
						
						if (0 == str.length()) {
							continue;
						}
					}
					
					
					List<TAccountBalance> tAccountBalanceList = null;
					int nowPage = 1;
					Integer deFaultPageSize = PayCenterConstant.DAILY_PAGESIZE;
					
					TAccountBalanceExample tAccountBalanceExample = new TAccountBalanceExample();
					Page page = PageHelper.startPage(nowPage,deFaultPageSize);
					tAccountBalanceList = tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
					
					int pages = page.getPages();
					int pageNum = page.getPageNum();
					for(;pageNum<=pages;pageNum++){
						nowPage++;
						for(TAccountBalance tAccountBalance:tAccountBalanceList){
							Long itemAccountId = tAccountBalance.getAccountId();
							Boolean isFailRecord = false;
							for(Long accountId:accountIdList){
								if(itemAccountId.longValue() == accountId.longValue()){
									isFailRecord = true;
								}
							}
							if(!isFailRecord){
								TAccountBalanceSnapshot record = new TAccountBalanceSnapshot();
								record.setBalanceType(tAccountBalance.getBalanceType());
								record.setAmount(tAccountBalance.getAmount());
								record.setAccountId(tAccountBalance.getAccountId());
								record.setUserId(tAccountBalance.getUserId());
								
								tAccountBalanceSnapshotMapper.insert(record);
							}
						}
						
						page = PageHelper.startPage(nowPage,deFaultPageSize);
						tAccountBalanceList = tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
					}
					
					tDailyTask.setStatus(DailyTaskStatus.DEAL_DONE.getValue());
					
					tDailyTaskMapper.updateByPrimaryKeySelective(tDailyTask);
				}
			} catch (Exception e) {
				throw e;
			}finally{
				if(null != br){
					br.close();
				}
				if(null != isr){
					isr.close();
				}
				if(null != fis){
					fis.close();
				}
				if(null != file){
					file.delete();
				}
			}
		}else{
			//出入金
			TDailyPaycenterResultExample tDailyPaycenterResultExample = new TDailyPaycenterResultExample();
			tDailyPaycenterResultExample.createCriteria().andDealDateEqualTo(tDailyTask.getDealDate());
			
			tDailyPaycenterResultMapper.deleteByExample(tDailyPaycenterResultExample);
			
			try {
				if(null != file){
					fis = new FileInputStream(file);
					
					isr = new InputStreamReader(fis);
					br = new BufferedReader(isr);
					String str = "";
					int count = 0;
					while ((str = br.readLine()) != null) {
						try {
							// 这里是去掉文件头里自带编码,不能去掉
							if (str != null) {
								str = str.replaceAll("\ufeff", "");
							}
							//第一行不读取
							if (1 > count++) {
								continue;
							}
							
							TDailyPaycenterResult tDailyPaycenter = JSON.parseObject(str, TDailyPaycenterResult.class);
							
							tDailyPaycenter.setDealDate(tDailyTask.getDealDate());
							tDailyPaycenterResultMapper.insert(tDailyPaycenter);
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("解析异常: " + str);
						}
						
						if (0 == str.length()) {
							continue;
						}
					}
					tDailyTask.setStatus(DailyTaskStatus.DEAL_DONE.getValue());
					
					tDailyTaskMapper.updateByPrimaryKeySelective(tDailyTask);
				}
			} catch (Exception e) {
				throw e;
			}finally{
				if(null != br){
					br.close();
				}
				if(null != isr){
					isr.close();
				}
				if(null != fis){
					fis.close();
				}
				if(null != file){
					file.delete();
				}
			}
		}
	}
}
