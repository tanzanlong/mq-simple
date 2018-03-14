package com.baibei.accountservice.settlement.business;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceSnapshotMapper;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountBalanceSnapshot;
import com.baibei.accountservice.model.TAccountBalanceSnapshotExample;

@Service
public class BalanceSnapShotBusiness {

	static final Logger logger = LoggerFactory.getLogger(BalanceSnapShotBusiness.class);

	@Autowired
	TAccountBalanceMapper tAccountBalanceMapper;
	
	@Autowired
	TAccountBalanceSnapshotMapper tAccountBalanceSnapshotMapper;

	public void generateBalanceSnapShotData(){
		try {
			
			TAccountBalanceExample tAccountBalanceExample = new TAccountBalanceExample();
			
			List<TAccountBalance> tAccountBalanceList = tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
			if(!CollectionUtils.isEmpty(tAccountBalanceList)){
				
				TAccountBalanceSnapshotExample tAccountBalanceSnapshotExample = new TAccountBalanceSnapshotExample();
				tAccountBalanceSnapshotMapper.deleteByExample(tAccountBalanceSnapshotExample);
				
				logger.info("删除余额快照表中的数据!");
				
				for(TAccountBalance tAccountBalance:tAccountBalanceList){
					TAccountBalanceSnapshot record = new TAccountBalanceSnapshot();
					record.setBalanceType(tAccountBalance.getBalanceType());
					record.setAmount(tAccountBalance.getAmount());
					record.setAccountId(tAccountBalance.getAccountId());
					record.setUserId(tAccountBalance.getUserId());
					
					tAccountBalanceSnapshotMapper.insert(record);
				}
				logger.info("生成余额快照数据完成");
			}else{
				logger.info("余额表中没有数据");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("生成余额快照数据异常"+e);
		}
	}
}
