package com.baibei.accountservice.account.scheduler;

import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceOnthewayMapper;
import com.baibei.accountservice.model.TAccountBalanceOntheway;
import com.baibei.accountservice.model.TAccountBalanceOnthewayExample;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
/**
 * 定时程序为账户异步更新的后补方案，在MQ不可用或MQ丢消息时不致影响余额变更，需要注意消息重复消费的处理，可靠性100%
 * @author peng
 */
public class BalanceUpdateScheduler {

    @Autowired
    TAccountBalanceOnthewayMapper tAccountBalanceOnthewayMapper;
    
    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;
    
    @Autowired
    SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    AccountBusiness accountBusiness;

    Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();


    @Scheduled(cron = "0/1 * *  * * ? ")
    public void updateBalance(){
        if(schedulerMasterCheck.isMaster()){
            for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
                String exchangeTag = entry.getKey();
                DateSourceLocal.setExchangeTag(exchangeTag);
                doUpdate();
            }
        }
    }


    private void doUpdate() {
        TAccountBalanceOnthewayExample tAccountBalanceOnthewayExample = new TAccountBalanceOnthewayExample();
        tAccountBalanceOnthewayExample.createCriteria().andIsHandleEqualTo(0);
        PageHelper.startPage(0, 500);
        List<TAccountBalanceOntheway> list = tAccountBalanceOnthewayMapper.selectByExample(tAccountBalanceOnthewayExample);
        try{
            handle(list);
        }catch(Exception e){
            log.error(e.getMessage());
        }
    }
    
    private void handle(List<TAccountBalanceOntheway> list){
        if(CollectionUtils.isNotEmpty(list)){
            for(TAccountBalanceOntheway item : list){//使用简单方案，一条一条执行，后期按实际需要再考虑批量处理
                try{
                    boolean result = accountBusiness.applyBalanceOnTheWay(item);
                    if(result == false){//失败不更新状态，下一次定时任务时仍会被执行
                        log.warn("Scheduler applyBalanceOnTheWay {} fail", item.getMsgId());
                    }
                }catch(Exception e){
                    log.error(e.getMessage());
                }
            }
        }
    }
}
