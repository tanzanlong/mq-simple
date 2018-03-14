package com.baibei.accountservice.settlement.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.dao.TAccountCashierOrderMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SettlementBusiness {

    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;
    
    @Autowired
    TAccountMapper tAccountMapper;
    
    @Autowired
    TAccountCashierOrderMapper tAccountCashierOrderMapper;
    
    @Autowired
    TAccountCashierLogMapper tAccountCashierLogMapper;
    
    /**
     * 统计账户数量
     * @return
     */
    public int countAccountSize(){
        return tAccountMapper.countAccountSize();
    }
    
    /**
     * 分页查询账户ID列表
     * @param page
     * @param size
     * @return
     */
    public List<Long> qryAccountIdList(int page, int size){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("size", size);
        param.put("offset", page*size);
        List<TAccount> accountList = tAccountMapper.selectAccountIdByPage(param);
        if(CollectionUtils.isNotEmpty(accountList)){
            List<Long> accountIdList = new ArrayList<Long>(accountList.size());
            for(TAccount tAccount : accountList){
                accountIdList.add(tAccount.getAccountId());
            }
            return accountIdList;
        }else{
            return null;
        }
    }
    
    /**
     * 按账户ID列表返回账户余额列表
     * @param accountIdList
     * @return
     */
    public List<TAccountBalance> qryAccountBalanceList(List<Long> accountIdList){
        TAccountBalanceExample example = new TAccountBalanceExample();
        List<String> balanceTypeList = new ArrayList<String>();
        balanceTypeList.add(Constants.BALANCE_TYPE_AVALIABLE);
        balanceTypeList.add(Constants.BALANCE_TYPE_FREEZON);
        example.createCriteria().andAccountIdIn(accountIdList).andBalanceTypeIn(balanceTypeList);
        return tAccountBalanceMapper.selectByExample(example);
    }
    
    /**
     * 统计日结算订单总量
     * @param startTime
     * @param endTime
     * @return
     */
    public int countCashierOrderSizeByTime(Date startTime, Date endTime){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("beginDate", startTime);
        params.put("endDate", endTime);
        return tAccountCashierOrderMapper.countCashierOrderSizeByTime(params);
    }
    
    /**
     * 分页查询日结算订单
     * @param page
     * @param size
     * @return
     */
    public List<TAccountCashierOrder> qryAccountCashierOrderList(int page, int size, Date beginDate, Date endDate){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("size", size);
        param.put("offset", page*size);
        param.put("beginDate", beginDate);
        param.put("endDate", endDate);
        return tAccountCashierOrderMapper.qryAccountCashierOrderList(param);
    }
    
    /**
     * 按结算订单列表查询日结算数据
     * @param cashierOrderList
     * @return
     */
    public List<TAccountCashierLog> qryCashierLogList(List<TAccountCashierOrder> cashierOrderList){
//        Map<String, TAccountCashierOrder> orderTypeAndIdMap = new HashMap<>();
        //按orderId列表查询
        Set<String> orderIdList = new HashSet<>(cashierOrderList.size());
        for(TAccountCashierOrder tAccountCashierOrder : cashierOrderList){
            orderIdList.add(tAccountCashierOrder.getOrderId());
//            String key = tAccountCashierOrder.getOrderType() + "$" + tAccountCashierOrder.getOrderId();
//            orderTypeAndIdMap.put(key, tAccountCashierOrder);
        }
        TAccountCashierLogExample example = new TAccountCashierLogExample();
        example.createCriteria().andOrderIdIn(new ArrayList<>(orderIdList));
        example.setOrderByClause("id");
        List<TAccountCashierLog> list = tAccountCashierLogMapper.selectByExample(example);

        return list;

        // 作废，只需要按订单号产生所有费项即可
        //注意，由于order_id+order_type才惟一确定一个订单，故需要将order_id相同，order_type为非指定的记录过滤掉
//        List<TAccountCashierLog> resultList = new ArrayList<>();
//        if(CollectionUtils.isNotEmpty(list)){
//            for(TAccountCashierLog tAccountCashierLog : list){
//                String key = tAccountCashierLog.getOrderType() + "$" + tAccountCashierLog.getOrderId();
//                if(orderTypeAndIdMap.containsKey(key)){
//                    resultList.add(tAccountCashierLog);
//                }
//            }
//        }
//        return resultList;
    }
}
