package com.baibei.accountservice.account.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.account.dto.exception.TicketUsedException;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.comm.TicketStatusEnum;
import com.baibei.accountservice.dao.TAccountCashierOrderMapper;
import com.baibei.accountservice.dao.TTicketMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountCashierOrder;
import com.baibei.accountservice.model.TTicket;
import com.baibei.accountservice.model.TTicketExample;
import com.baibei.accountservice.util.DateUtil;
import com.baibei.accountservice.util.SnowflakeIdWorker;
import com.baibei.accountservice.vo.cb.LeftTicketValue;
import com.baibei.accountservice.vo.cb.TicketDelete;
import com.baibei.accountservice.vo.cb.TicketGive;
import com.baibei.accountservice.vo.cb.TicketInStore;
import com.baibei.accountservice.vo.cb.TicketInStore.Item;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TicketBusiness {

    @Autowired 
    TTicketMapper tTicketMapper;
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    TAccountCashierOrderMapper tAccountCashierOrderMapper;
    
    //入库
    public boolean instore(TicketInStore ticketInStore){
        List<Item> itemList = ticketInStore.getItemList();
        Date date = new Date();
        TAccount tAccount = accountBusiness.qryAccountByUserId(ticketInStore.getUserId());
        if(tAccount == null){
            throw new IllegalArgumentException("账户不存在");
        }
        
        Long accountId = tAccount.getAccountId();
        List<TTicket> ticketList = new ArrayList<TTicket>(itemList.size());
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        String batchNo = snowflakeIdWorker.nextId() + "";
        for(Item item : itemList){
            for(int i=0; i<item.getAmount(); i++){
                TTicket ticket = new TTicket();
                ticket.setId("" + snowflakeIdWorker.nextId());
                ticket.setAccountId(accountId);
                ticket.setBusinessType(ticketInStore.getBusinessType());
                ticket.setCreateTime(date);
                ticket.setEffectiveTime(DateUtil.getMaxTimeStamp());
                ticket.setExpireTime(DateUtil.getMaxTimeStamp());
                ticket.setSellerAccountId(accountId);
                ticket.setSellerUserId(ticketInStore.getUserId());
                ticket.setTicketStatus(TicketStatusEnum.INIT.getCode());
                ticket.setTicketType(item.getTicketType());
                ticket.setTicketValue(item.getTicketValue());
                ticket.setUpdateTime(date);
                ticket.setUserId(ticketInStore.getUserId());
                ticket.setTicketName(item.getTicketName());
                ticket.setBatchNo(batchNo);
                ticket.setTicketFaceValue(item.getTicketFaceValue());
                ticketList.add(ticket);
            }
        }
        tTicketMapper.batchInsert(ticketList);
        return true;
    }
    
    //送券
    @Transactional
    public boolean give(TicketGive ticketGive){
        //收券人账户
        TAccount receiveAccount = accountBusiness.qryAccountByUserId(ticketGive.getReceiveUserId());
        if(receiveAccount == null){
            throw new IllegalArgumentException("收券人账户不存在");
        }
        
        //这里使用了一个小技巧，即使用一个惟一的batchNo，将每条送券的记录都标识为此值。后续通过查询总数量判断是否送券实际执行结果符合预期，如不符合预期，则整体回退。
        //这里将压力都交给了DB，不符合特别高并发量的请求，而送券属于中低并发请求，可采用
        //批量送券
        List<TicketGive.Item> itemList = ticketGive.getItemList();
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        String batchNo = snowflakeIdWorker.nextId() + "";
        int total = 0;
        for(TicketGive.Item item : itemList){
            total += item.getAmount();
            Map<String, Object> params = new HashMap<>();
            params.put("batchNo", batchNo);
            params.put("amount", item.getAmount());
            params.put("sellerUserId", ticketGive.getOwnerUserId());
            params.put("userId", ticketGive.getReceiveUserId());
            params.put("accountId", receiveAccount.getAccountId());
            params.put("effectiveTime", item.getEffectiveTime());
            params.put("expireTime", item.getExpireTime());
            params.put("ticketType", item.getTicketType());
            //批量赠送
            tTicketMapper.batchGive(params);
        }
        
        //查询实际执行的送券总数量与期望数量是否不一致，如不一致则回退
        TTicketExample example = new TTicketExample();
        example.createCriteria().andBatchNoEqualTo(batchNo).andSellerUserIdEqualTo(ticketGive.getOwnerUserId());
        if(tTicketMapper.countByExample(example) != total){
            throw new IllegalArgumentException("送券失败,券数量不足");
        }
        return true;
    }
    
    //分页查询
    public List<TTicket> qryPage(String sellerUserId, int pageNo, int pageSize){
        TTicketExample example = new TTicketExample();
        example.createCriteria().andSellerUserIdEqualTo(sellerUserId).andTicketStatusEqualTo(TicketStatusEnum.INIT.getCode());
        PageHelper.startPage(pageNo, pageSize);
        return tTicketMapper.selectByExample(example);
    }
    
    //查询某用户券,1=有效,0=无效，2=所有
    public List<TTicket> queryAvaliableTicketList(String userId, int status){
        TTicketExample example = new TTicketExample();
        Date date = new Date();
        if(status == 1){
            example.createCriteria().andUserIdEqualTo(userId).andTicketStatusEqualTo(TicketStatusEnum.GIVED.getCode()).andExpireTimeGreaterThan(date);
        }else if(status == 0){
            List<String> statusList = new ArrayList<>();
            statusList.add(TicketStatusEnum.EXPIRED.getCode());
            statusList.add(TicketStatusEnum.USED.getCode());
            example.createCriteria().andUserIdEqualTo(userId).andTicketStatusIn(statusList);
        }else{
            example.createCriteria().andUserIdEqualTo(userId);
        }
        return tTicketMapper.selectByExample(example);
    }
    
    //查询剩余可发行券价值
    public LeftTicketValue qryLeftTicketValue(String userId){
        LeftTicketValue leftTicketValue = new LeftTicketValue();
        
        //保证金金额
        long bondsAmount = 0;
        List<TAccountBalance> list = accountBusiness.qryRealTimeBalance(userId);
        if(CollectionUtils.isNotEmpty(list)){
            for(TAccountBalance tAccountBalance : list){
                if(Constants.BALANCE_TYPE_TICKET_BONDS.equalsIgnoreCase(tAccountBalance.getBalanceType())){
                    bondsAmount += tAccountBalance.getAmount();
                    break;
                }
            }
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        Long unusedTicketTotalValue = tTicketMapper.sumUnusedTicketValue(params);
        
        leftTicketValue.setTicketBonds(bondsAmount);
        leftTicketValue.setUsedTicketValue(unusedTicketTotalValue==null ? 0:unusedTicketTotalValue);
        return leftTicketValue;
    }
    
    //用券
    @Transactional
    public boolean useTickets(String orderType, String orderId, String userId, List<String> ticketIdList){
        //幂等检查
        if(accountBusiness.checkTransIsNotExists(orderType, orderId)){
            Date date = new Date();
            //插入订单表，如果订单复复则会因为惟一索引冲突报异常
            TAccountCashierOrder order = new TAccountCashierOrder();
            order.setCreateTime(date);
            order.setOrderId(orderId);
            order.setOrderType(orderType);
            tAccountCashierOrderMapper.insert(order);
            
            for(String ticketId : ticketIdList){
                TTicket ticket = tTicketMapper.selectByPrimaryKey(ticketId);
                if(ticket == null){
                    throw new IllegalArgumentException("券不存在");
                }
                if(ticket.getUserId().equals(userId) && ticket.getTicketStatus().equalsIgnoreCase(TicketStatusEnum.GIVED.getCode())){//是本人的券，且状态未使用未过期
                    ticket.setTicketStatus(TicketStatusEnum.USED.getCode());
                    ticket.setUpdateTime(date);
                    tTicketMapper.updateByPrimaryKey(ticket);
                }else{
                    throw new TicketUsedException("券已使用过");
                }
            }
            return true;
        }else{
            log.warn("Duplicate orderId {}", orderId);
        }
        return true;
    }
    
    public void doExpire(TTicket ticket){
        ticket.setTicketStatus(TicketStatusEnum.EXPIRED.getCode());
        ticket.setUpdateTime(new Date());
        tTicketMapper.updateByPrimaryKey(ticket);
    }
    
    //入库
    public boolean delete(TicketDelete ticketDelete){
        TAccount tAccount = accountBusiness.qryAccountByUserId(ticketDelete.getUserId());
        if(tAccount == null){
            throw new IllegalArgumentException("账户不存在");
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sellerUserId", ticketDelete.getUserId());
        params.put("ticketType", ticketDelete.getTicketType());
        params.put("ticketStatus", TicketStatusEnum.DELETED.getCode());
        tTicketMapper.updateStatusBatch(params);
        return true;
    }
    
    
    //用券
    @Transactional
    public boolean rollbackUseTickets(String orderType, String orderId, String userId, List<String> ticketIdList){
        //幂等检查
        if(accountBusiness.checkTransIsNotExists(orderType, orderId)){
            Date date = new Date();
            //插入订单表，如果订单复复则会因为惟一索引冲突报异常
            TAccountCashierOrder order = new TAccountCashierOrder();
            order.setCreateTime(date);
            order.setOrderId(orderId);
            order.setOrderType(orderType);
            tAccountCashierOrderMapper.insert(order);
            
            for(String ticketId : ticketIdList){
                TTicket ticket = tTicketMapper.selectByPrimaryKey(ticketId);
                if(ticket == null){
                    throw new IllegalArgumentException("券不存在");
                }
                if(ticket.getUserId().equals(userId) && ticket.getTicketStatus().equalsIgnoreCase(TicketStatusEnum.USED.getCode())){//是本人的券，且状态为已使用
                    ticket.setTicketStatus(TicketStatusEnum.GIVED.getCode());
                    ticket.setUpdateTime(date);
                    tTicketMapper.updateByPrimaryKey(ticket);
                }
            }
            return true;
        }else{
            log.warn("Duplicate orderId {}", orderId);
        }
        return true;
    }
}
