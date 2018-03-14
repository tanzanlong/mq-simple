package com.baibei.accountservice.controller.cb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baibei.accountservice.account.business.TicketBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TTicketMapper;
import com.baibei.accountservice.model.TTicket;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.LeftTicketValue;
import com.baibei.accountservice.vo.cb.RollbackTicket;
import com.baibei.accountservice.vo.cb.TicketDelete;
import com.baibei.accountservice.vo.cb.TicketGive;
import com.baibei.accountservice.vo.cb.TicketInStore;
import com.baibei.accountservice.vo.cb.TicketItem;
import com.baibei.accountservice.vo.cb.TicketPageQryReq;
import com.ctrip.framework.apollo.core.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@EnableAutoConfiguration
@RequestMapping("/account/ticket")
@Slf4j
public class TicketController {
    
    @Autowired
    TTicketMapper tTicketMapper;
    
    @Autowired
    TicketBusiness ticketBusiness;
    
    @RequestMapping(value = "/instore")
    public BaseResponse<Boolean> instore(@RequestBody TicketInStore ticketInStore){
        try{
            checkParam(ticketInStore);
            ticketBusiness.instore(ticketInStore);
            return RspUtils.success(true);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    private void checkParam(TicketInStore ticketInStore){
        if(StringUtils.isBlank(ticketInStore.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(ticketInStore.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(CollectionUtils.isEmpty(ticketInStore.getItemList())){
            throw new IllegalArgumentException("parameter itemList can not be empty");
        }
    }
    
    @RequestMapping(value = "/give")
    public BaseResponse<Boolean> give(@RequestBody TicketGive ticketGive){
        try{
            checkParam(ticketGive);
            ticketBusiness.give(ticketGive);
            return RspUtils.success(true);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
    
    private void checkParam(TicketGive ticketGive){
        if(StringUtils.isBlank(ticketGive.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(ticketGive.getOwnerUserId())){
            throw new IllegalArgumentException("parameter ownerUserId can not be blank");
        }
        if(StringUtils.isBlank(ticketGive.getReceiveUserId())){
            throw new IllegalArgumentException("parameter receriveUserId can not be blank");
        }
        if(CollectionUtils.isEmpty(ticketGive.getItemList())){
            throw new IllegalArgumentException("parameter itemList can not be empty");
        }
    }
    
    @RequestMapping(value = "/querypage")
    public BaseResponse<List<TicketItem>> querypage(@RequestBody TicketPageQryReq ticketPageQryReq){
        try{
            List<TTicket> ticketList = ticketBusiness.qryPage(ticketPageQryReq.getUserId(), ticketPageQryReq.getPageNo(), ticketPageQryReq.getPageSize());
            return RspUtils.success(toTicketItemList(ticketList));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
    
    @RequestMapping(value = "/query")
    public BaseResponse<List<TicketItem>> query(@RequestBody TicketPageQryReq ticketPageQryReq){
        try{
            List<TTicket> ticketList = ticketBusiness.queryAvaliableTicketList(ticketPageQryReq.getUserId(), ticketPageQryReq.getStatus());
            return RspUtils.success(toTicketItemList(ticketList));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
    
    @RequestMapping(value = "/leftTicketValue")
    public BaseResponse<LeftTicketValue> leftTicketValue(@RequestBody TicketPageQryReq ticketPageQryReq){
        try{
            return RspUtils.success(ticketBusiness.qryLeftTicketValue(ticketPageQryReq.getUserId()));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
    
    private List<TicketItem> toTicketItemList(List<TTicket> ticketList){
        List<TicketItem> ticketItemList = new ArrayList<TicketItem>();
        if(!CollectionUtils.isEmpty(ticketList)){
            for(TTicket ticket : ticketList){
                ticketItemList.add(toTicketItem(ticket));
            }
        }
        return ticketItemList;
    }
    
    private TicketItem toTicketItem(TTicket ticket){
        TicketItem ticketItem = new TicketItem();
        ticketItem.setId(ticket.getId()); 
        ticketItem.setEffectiveTime(ticket.getEffectiveTime());
        ticketItem.setExpireTime(ticket.getExpireTime());
        ticketItem.setTicketFaceValue(ticket.getTicketFaceValue());
        ticketItem.setTicketName(ticket.getTicketName());
        ticketItem.setTicketType(ticket.getTicketType());
        ticketItem.setTicketValue(ticket.getTicketValue());
        ticketItem.setTicketStatus(ticket.getTicketStatus());
        return ticketItem;
    }
    
    private void checkParam(TicketDelete ticketDelete){
        if(StringUtils.isBlank(ticketDelete.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(ticketDelete.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(StringUtils.isBlank(ticketDelete.getTicketType())){
            throw new IllegalArgumentException("parameter ticketType can not be blank");
        }
    }
    
    @RequestMapping(value = "/delete")
    public BaseResponse<Boolean> delete(@RequestBody TicketDelete ticketDelete){
        try{
            checkParam(ticketDelete);
            ticketBusiness.delete(ticketDelete);
            return RspUtils.success(true);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
    
    private void checkParam(RollbackTicket rollbackTicket){
        if(StringUtils.isBlank(rollbackTicket.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(rollbackTicket.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if(CollectionUtils.isEmpty(rollbackTicket.getTicketIdList())){
            throw new IllegalArgumentException("parameter ticketIdList can not be empty");
        }
    }
    
    @RequestMapping(value = "/rollbackUseTicket")
    public BaseResponse<Boolean> rollbackUseTicket(@RequestBody RollbackTicket rollbackTicket){
        try{
            checkParam(rollbackTicket);
            return RspUtils.success(ticketBusiness.rollbackUseTickets(Constants.ORDER_TYPE_OPENPOSITION_ROLLBACK, rollbackTicket.getOrderId(), rollbackTicket.getUserId(), rollbackTicket.getTicketIdList()));
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    } 
}
