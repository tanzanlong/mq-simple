package com.baibei.accountservice.account.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baibei.accountservice.multidatasource.DateSourceLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.business.TicketBusiness;
import com.baibei.accountservice.account.comm.TicketStatusEnum;
import com.baibei.accountservice.comm.SchedulerMasterCheck;
import com.baibei.accountservice.dao.TTicketMapper;
import com.baibei.accountservice.model.TTicket;
import com.baibei.accountservice.model.TTicketExample;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
public class TicketExpiredScheduler {

    @Autowired
    SchedulerMasterCheck schedulerMasterCheck;
    
    @Autowired
    TicketBusiness ticketBusiness;
    
    @Autowired
    TTicketMapper tTicketMapper;

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
        TTicketExample example = new TTicketExample();
        example.createCriteria().andTicketStatusEqualTo(TicketStatusEnum.GIVED.getCode()).andExpireTimeLessThanOrEqualTo(new Date());
        PageHelper.startPage(0, 500);
        List<TTicket> ticketList = tTicketMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(ticketList)){
            for(TTicket ticket : ticketList){
                ticketBusiness.doExpire(ticket);
            }
        }
    }
    
}
