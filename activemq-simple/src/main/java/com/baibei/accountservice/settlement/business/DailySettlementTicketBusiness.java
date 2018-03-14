package com.baibei.accountservice.settlement.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.account.comm.Constants.DailyTaskStatus;
import com.baibei.accountservice.dao.TDailyTaskMapper;
import com.baibei.accountservice.dao.TTicketMapper;
import com.baibei.accountservice.model.TDailyTask;
import com.baibei.accountservice.model.TDailyTaskExample;
import com.baibei.accountservice.model.TTicket;
import com.baibei.accountservice.model.TTicketExample;
import com.baibei.accountservice.paycenter.common.Constants.DailyTaskType;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.utill.FTPUtil;
import com.github.pagehelper.PageHelper;

@Service
public class DailySettlementTicketBusiness {

	static final Logger logger = LoggerFactory.getLogger(DailySettlementTicketBusiness.class);

	static final int pageSize = 500;
	
	@Autowired
	DailyTaskConfig dailyTaskConfig;
	
	@Autowired
	TDailyTaskMapper tDailyTaskMapper;
	
	@Autowired
	TTicketMapper tTicketMapper;
	
	@Autowired
	SettlementBusiness settlementBusiness;

	//获取组装报文需要的数据
	public void getData4AssemblyMessage(String businessType, Date beginDate, Date endDate){
		try {
			String writeFileName = FTPUtil.generateFileName(PayCenterConstant.DAILY_SETTLEMENTTICKET_FILENAME_PREFIX, null, null, businessType,beginDate);
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
			    TTicketExample tTicketExample = new TTicketExample();
                int totalRecord = tTicketMapper.countByExample(tTicketExample);
                int totalPage = toPage(totalRecord);
                String filleFileName = writeTempFilePath + "/" + writeFileName;
                File f = new File(filleFileName);
                BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
                tTicketExample.setOrderByClause("id");
                for(int currentPage=0; currentPage<totalPage; currentPage++){
                    //分页查出订单
                    PageHelper.startPage(currentPage, pageSize);
                    List<TTicket> ticketList = tTicketMapper.selectByExample(tTicketExample);
                    if(CollectionUtils.isNotEmpty(ticketList)){
                        if(CollectionUtils.isNotEmpty(ticketList)){
                            StringBuilder sb = format(ticketList, DateUtils.addDays(beginDate, -3));
                            bw.write(sb.toString());
                            bw.flush();
                        }
                    }
                }
                IOUtils.closeQuietly(bw);
                logger.info("generate daily ticket settlement [{}] file time: {} MS", totalRecord, System.currentTimeMillis() - startTime);
				
				if(0 < totalPage){
					boolean uploadRes = FTPUtil.uploadFileToFTP(ftpHost, ftpPort, ftpUserName, ftpPassword, writeFileName, writeTempFilePath,remoteFilePath,ftpProtocol);
					if(uploadRes){
						TDailyTask record = new TDailyTask ();
						record.setTaskType(DailyTaskType.SETTLEMENTTICKET.getValue());
						record.setFileName(writeFileName);
						record.setStatus(DailyTaskStatus.INIT.getValue());
						record.setDealDate(beginDate);
						
						tDailyTaskMapper.insert(record);
						logger.info("日结算券对账文件已生成!文件名是:"+writeFileName);
					}
				}
			}
		} catch (Exception e) {
			logger.info("生成日结算券对账文件异常"+e);
		}
	}
	
	//将一批数据转成多行文件格式，放入StringBuilder
    private StringBuilder format(List<TTicket> ticketList, Date targetDate){
        StringBuilder sb = new StringBuilder();
        for(TTicket ticket : ticketList){
            if(ticket.getUpdateTime().after(targetDate)){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", ticket.getId());
                map.put("ticketType", ticket.getTicketType());
                map.put("userId", ticket.getUserId());
                map.put("sellerUserId", ticket.getSellerUserId());
                map.put("accountId", ticket.getAccountId());
                map.put("sellerAccountId", ticket.getSellerAccountId());
                map.put("businessType", ticket.getBusinessType());
                map.put("ticketStatus", ticket.getTicketStatus());
                map.put("createTime", ticket.getCreateTime());
                sb.append(JSON.toJSONString(map));
                sb.append("\r\n");
            }
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
