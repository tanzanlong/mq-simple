package com.baibei.accountservice.settlement.business.executor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.baibei.accountservice.model.TAccountBalance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 日余额对账生产者
 * 从queue里取消息，然后生成文件
 * @author peng
 */
@Slf4j
public class DailyBalanceProducer implements Runnable {

    //业务类型
    private String businessType;
    
    //生产者编号
    private int producerId;
    
    //日余额对账执行器
    private DailyBalanceExecutor executor; 
    
    public DailyBalanceProducer(DailyBalanceExecutor executor, int producerId, String businessType){
        this.executor = executor;
        this.producerId = producerId;
        this.businessType = businessType;
    }
   
    @Override
    public void run() {
        log.info("DailyBalanceProducer {} start", producerId);
        int page = 0;
        while(page > -1){
            int size = executor.getPageSize();
            log.info("DailyBalanceProducer handle page {}", page);
            List<Long> accountIdList = executor.getSettlementBusiness().qryAccountIdList(page, size);
            if(CollectionUtils.isNotEmpty(accountIdList)){
                List<TAccountBalance> list = executor.getSettlementBusiness().qryAccountBalanceList(accountIdList);
                if(CollectionUtils.isNotEmpty(list)){
                    StringBuilder sb = format(list);
                    try {
                        executor.getBlockingQueue().put(sb);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                }
            }
            page = executor.nextProducerPage();
        }
        log.info("DailyBalanceProducer {} end", producerId);
    }
    
    //将一批数据转成多行文件格式，放入StringBuilder
    private StringBuilder format(List<TAccountBalance> accountBalanceList){
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
    
    @Data
    public static class DailyBalanceItem{
        private String businessType;
        private String userId;
        private Long accountId;
        private Long balance;
    }
}
