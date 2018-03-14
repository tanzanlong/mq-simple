package com.baibei.accountservice.account.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baibei.account.dto.exception.BalanceNotEnoughException;
import com.baibei.account.dto.request.CreateOrgAccountRequest;
import com.baibei.account.dto.response.Balance;
import com.baibei.account.dto.response.BalanceAndSignedStatus;
import com.baibei.account.dto.response.BalanceSummary;
import com.baibei.account.dto.response.FeeAndInterest;
import com.baibei.account.dto.response.OrgBalanceSummary;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.util.CustomerUtil;
import com.baibei.accountservice.account.vo.AccountBalanceModifyReq;
import com.baibei.accountservice.account.vo.ValidatePasswordResult;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceOnthewayMapper;
import com.baibei.accountservice.dao.TAccountCashierLogMapper;
import com.baibei.accountservice.dao.TAccountCashierOrderMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TAccountPasswordMapper;
import com.baibei.accountservice.dao.TBankInfoMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountBalanceOntheway;
import com.baibei.accountservice.model.TAccountBalanceOnthewayExample;
import com.baibei.accountservice.model.TAccountCashierLog;
import com.baibei.accountservice.model.TAccountCashierLogExample;
import com.baibei.accountservice.model.TAccountCashierOrder;
import com.baibei.accountservice.model.TAccountCashierOrderExample;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TAccountPassword;
import com.baibei.accountservice.model.TAccountPasswordExample;
import com.baibei.accountservice.model.TBankInfo;
import com.baibei.accountservice.model.TBankInfoExample;
import com.baibei.accountservice.paycenter.bussiness.ch.WithdrawBussiness;
import com.baibei.accountservice.rocketmq.RocketMQUtils;
import com.baibei.accountservice.util.MD5;
import com.baibei.push.server.provider.SmsPushProvider;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountBusiness {

    @Autowired 
    TAccountMapper tAccountMapper;
    
    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;
    
    @Autowired
    TAccountBalanceOnthewayMapper tAccountBalanceOnthewayMapper;
    
    @Autowired
    TAccountCashierLogMapper tAccountCashierLogMapper;
    
    @Autowired
    TAccountPasswordMapper tAccountPasswordMapper;
    
    @Autowired
    RocketMQUtils rocketMQUtils;
    
    @Autowired
    TAccountCashierOrderMapper tAccountCashierOrderMapper;
    
    @Autowired
    WithdrawBussiness withdrawBussiness;
    
    @Autowired
    TBankInfoMapper tBankInfoMapper;
    
    @Autowired
    SmsPushProvider smsPushProvider;
    
    
    /**
     * 按用户ID查询账户ID
     * @param userId
     * @return
     */
    public Long qryAccountIdByUserId(String userId){
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<TAccount> list = tAccountMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0).getAccountId();
        }
        return null;
    }
    
    /**
     * 查询广清账户
     * @param userId
     * @return
     */
    public TAccount qryClearCenterAccount(){
        TAccountExample example = new TAccountExample();
        example.createCriteria().andIsDelEqualTo(0).andOrgTypeEqualTo(CreateOrgAccountRequest.TYPE_CLEARCENTER);
        List<TAccount> list = tAccountMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        throw new IllegalArgumentException("广清账户不存在");
    }
    
    /**
     * 余额变更，并发送MQ
     * @param req
     * @return
     */
    @Transactional
    public Boolean modifyBalance(AccountBalanceModifyReq req){
        log.info(req.toString());
//        List<TAccountBalanceOntheway> accountBalanceOneTheWayList = modifyBalanceOnly(req);
//        if(CollectionUtils.isNotEmpty(accountBalanceOneTheWayList)){
//            for(TAccountBalanceOntheway accountBalanceOntheway : accountBalanceOneTheWayList){
//                //发MQ
//                try{
//                    BalanceUpdateMessage message = new BalanceUpdateMessage();
//                    message.setAccountId(accountBalanceOntheway.getAccountId());
//                    message.setAmount(accountBalanceOntheway.getAmount());
//                    message.setBlanceType(accountBalanceOntheway.getBalanceType());
//                    message.setUserId(accountBalanceOntheway.getUserId());
//                    message.setMessageId(accountBalanceOntheway.getMsgId());
//                    rocketMQUtils.send(Constants.TOPIC_UPDATE_ACCOUNT, Constants.TAG_UPDATE_ACCOUNT, accountBalanceOntheway.getAccountId() + "", JSON.toJSONString(message));
//                }catch(Exception e){
//                    log.error(e.getMessage());
//                }
//            }
//        }
        //幂等检查
        if(checkTransIsNotExists(req.getOrderType(), req.getOrderId())){
            try{
                modifyBalanceOnly(req);
            }catch(BalanceNotEnoughException e){
                log.error(" modifyBalance  BalanceNotEnoughException:{}",e);
               throw new BalanceNotEnoughException("用户余额不足");
            }
            
        }else{
            log.warn("Duplicate orderId {}", req.getOrderId());
        }
        return true;
    }
    
    /**
     * 余额变更(DB事务操作)
     * @param req
     * @return
     */
    public List<TAccountBalanceOntheway> modifyBalanceOnly(AccountBalanceModifyReq req){
        Date date = new Date();
        Map<String, TAccountBalance> accountIdAndBlanceType2BalanceMap = new HashMap<String, TAccountBalance>();
        
        //插入订单表，如果订单复复则会因为惟一索引冲突报异常
        TAccountCashierOrder order = new TAccountCashierOrder();
        order.setCreateTime(date);
        order.setOrderId(req.getOrderId());
        order.setOrderType(req.getOrderType());
        tAccountCashierOrderMapper.insert(order);
        
        //取得账户列表
        Set<Long> accountIdList = new HashSet<Long>();
        //每个账户下面的余额类型
        Map<Long, Set<String>> accountId2BalanceTypeList = new HashMap<Long, Set<String>>(); 
        
        
        
        for(AccountBalanceModifyReq.Detail detail : req.getDetailList()){
            accountIdList.add(detail.getAccountId());
            Set<String> balanceTypeList = accountId2BalanceTypeList.get(detail.getAccountId());
            if(balanceTypeList == null){
                balanceTypeList = new HashSet<String>();
                accountId2BalanceTypeList.put(detail.getAccountId(), balanceTypeList);
            }
            balanceTypeList.add(detail.getBalanceType());
        }
        
        //取得每个账户每种余额类型的金额
        for(Long accountId : accountIdList){
            Set<String> balanceTypeList = accountId2BalanceTypeList.get(accountId);
            log.info("balanceTypeList is {}", balanceTypeList);
            List<TAccountBalance> balanceList = this.qryRealTimeBalance(accountId, new ArrayList<String>(balanceTypeList));
            if(CollectionUtils.isNotEmpty(balanceList)){
                for(TAccountBalance balance : balanceList){
                    String key = balance.getAccountId() + "$" + balance.getBalanceType();
                    accountIdAndBlanceType2BalanceMap.put(key, balance);
                }
            }else{
                throw new IllegalArgumentException("账户[" + accountId + "]余额记录不存在");
            }
        }
        
        List<TAccountBalanceOntheway> accountBalanceOneTheWayList = new ArrayList<TAccountBalanceOntheway>();
        //账务流水日志入库
        for(AccountBalanceModifyReq.Detail detail : req.getDetailList()){
            TAccountCashierLog accountCashierLog = new TAccountCashierLog();
            accountCashierLog.setOrderId(req.getOrderId());
            accountCashierLog.setCreateTime(date);
            accountCashierLog.setUpdateTime(date);
            accountCashierLog.setOrderType(req.getOrderType());
            
            accountCashierLog.setAccountId(detail.getAccountId());
            accountCashierLog.setBalanceType(detail.getBalanceType());
            accountCashierLog.setChangeAmount(detail.getAmount());
            accountCashierLog.setFeeItem(detail.getFeeItem());
            accountCashierLog.setUserId(detail.getUserId());
            accountCashierLog.setOrgId(detail.getOrgId());
            
            //取得change_before金额
            String key = detail.getAccountId() + "$" + detail.getBalanceType();
            TAccountBalance balance = accountIdAndBlanceType2BalanceMap.get(key);
            if(balance != null){
                accountCashierLog.setChangeBefore(balance.getAmount());
                Long newAmount = balance.getAmount() + detail.getAmount();
                if(newAmount < 0){
                    log.info("  账户[{}]类型为{}的余额不足balance.getAmount:{},detail.getAmount:{}",balance.getAccountId(),balance.getBalanceType()+":"+balance.getAmount()+":"+detail.getAmount());
                    throw new BalanceNotEnoughException("账户[" + detail.getAccountId() + "]类型为" + detail.getBalanceType() + "的余额不足:" + newAmount);
                }
                balance.setAmount(newAmount);
            }else{
                throw new IllegalArgumentException("账户[" + detail.getAccountId() + "]，类型为" + detail.getBalanceType() + "的余额记录不存在");
            }
            if(!detail.getBalanceType().equals(Constants.BALANCE_TYPE_LOAN) && !detail.getBalanceType().equals(Constants.BALANCE_TYPE_UNPAY)){//余额类型为融资/其他应付款时，只更新余额，无账务流水，因为流水已在可用余额中体现，不能重复记录
                tAccountCashierLogMapper.insert(accountCashierLog);
            }
            //在途余额
            TAccountBalanceOntheway accountBalanceOneTheWay = new TAccountBalanceOntheway();
            accountBalanceOneTheWay.setAccountId(detail.getAccountId());
            accountBalanceOneTheWay.setAmount(detail.getAmount());
            accountBalanceOneTheWay.setBalanceType(detail.getBalanceType());
            accountBalanceOneTheWay.setCreateTime(date);
            accountBalanceOneTheWay.setIsHandle(0);
            accountBalanceOneTheWay.setUpdateTime(date);
            accountBalanceOneTheWay.setUserId(detail.getUserId());
            accountBalanceOneTheWay.setMsgId(UUID.randomUUID().toString());
            accountBalanceOneTheWayList.add(accountBalanceOneTheWay);
        }
        for(TAccountBalanceOntheway accountBalanceOntheway : accountBalanceOneTheWayList){
            tAccountBalanceOnthewayMapper.insert(accountBalanceOntheway);
        }
        
        //Double check 取得每个账户每种余额类型的金额并检查是否<0
        for(Long accountId : accountIdList){
            Set<String> balanceTypeList = accountId2BalanceTypeList.get(accountId);
            List<TAccountBalance> balanceList = this.qryRealTimeBalance(accountId, new ArrayList<String>(balanceTypeList));
            if(!CollectionUtils.isEmpty(balanceList)){
                for(TAccountBalance balance : balanceList){
                    if(balance.getAmount() < 0){
                        log.info("  账户[{}]类型为{}的余额不足",balance.getAccountId(),balance.getBalanceType());
                        throw new BalanceNotEnoughException("账户[" + balance.getAccountId() + "]类型为" + balance.getBalanceType() + "的余额不足");
                    }
                }
            }
        }
        return accountBalanceOneTheWayList;
    }
    
    /**
     * 密码修改
     * 
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public boolean modifyPassword(String userId, String oldPassword, String newPassword) {
        boolean isSuccess = false;
        this.checkModifyPwdParam(userId, oldPassword, newPassword);
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(example);
        if (tAccounts == null || tAccounts.size() <= 0) {
            return isSuccess;
        }
        TAccount tAccount = tAccounts.get(0);
        TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
        tAccountPasswordExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
        List<TAccountPassword> tAccountPasswords =
                tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
        if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
            return isSuccess;
        }
        TAccountPassword tAccountPassword = tAccountPasswords.get(0);
        if (!MD5.sign(oldPassword, tAccountPassword.getSalt(), "utf-8").equals(
                tAccountPassword.getPassword())) {
            return isSuccess;
        }
        tAccountPassword.setUpdateTime(new Date());
        tAccountPassword.setSalt(AccountBusiness.getSalt());
        tAccountPassword.setPassword(MD5.sign(newPassword, tAccountPassword.getSalt(), "utf-8"));
        int count = tAccountPasswordMapper.updateByPrimaryKeySelective(tAccountPassword);
        if (count > 0) {
            isSuccess = true;
        }
        return isSuccess;
    }
    
    //密码检验
    public ValidatePasswordResult validatePassword(String userId, String password) {
        try{
            ValidatePasswordResult result = new ValidatePasswordResult();
            //总共可试次数
            int totalCount = 3;
            
            TAccountExample example = new TAccountExample();
            example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
            List<TAccount> tAccounts = tAccountMapper.selectByExample(example);
            if (tAccounts == null || tAccounts.size() <= 0) {
                throw new IllegalArgumentException("账户不存在");
            }
            
            TAccount tAccount = tAccounts.get(0);
            TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
            tAccountPasswordExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
            List<TAccountPassword> tAccountPasswords =
                    tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
            if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
                throw new IllegalArgumentException("账户密码不存在");
            }
            
            TAccountPassword tAccountPassword = tAccountPasswords.get(0);
            log.info("tAccountPassword is {}", tAccountPassword);
            log.info("error count is {}", tAccountPassword.getErrorCount());
            log.info("limit expired time is {}", tAccountPassword.getLimitExpireTime());
            if(tAccountPassword.getErrorCount() >= totalCount && new Date().before(tAccountPassword.getLimitExpireTime())){//次数达到最大可尝试次数且限制时间未到
                result.setValidateResult(false);
                result.setErrorCount(totalCount);
                result.setLeftCount(0);
                return result;
            }
            
            if (MD5.sign(password, tAccountPassword.getSalt(), "utf-8").equals(
                    tAccountPassword.getPassword())) {
                //重置errorCount为0
                tAccountPassword.setErrorCount(0);
                tAccountPasswordMapper.updateByPrimaryKey(tAccountPassword);
                
                result.setErrorCount(0);
                result.setLeftCount(totalCount);
                result.setValidateResult(true);
                return result;
            }else{
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, 1);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                
                tAccountPassword.setLimitExpireTime(c.getTime());
                tAccountPassword.setErrorCount(tAccountPassword.getErrorCount() + 1);
                tAccountPasswordMapper.updateByPrimaryKey(tAccountPassword);
                
                result.setLeftCount(totalCount - tAccountPassword.getErrorCount());
                result.setErrorCount(tAccountPassword.getErrorCount());
                result.setValidateResult(false);
                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 密码重置
     * 
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public boolean resetPassword(String userId,String mobile) {
        boolean isSuccess = false;
        if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
       /** if (mobile == null || mobile.trim().length() <= 6) {
            throw new IllegalArgumentException("parameter mobile is invalid");
        }**/
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(example);
        if (tAccounts == null || tAccounts.size() <= 0) {
            return isSuccess;
        }
        TAccount tAccount = tAccounts.get(0);
        TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
        tAccountPasswordExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
        List<TAccountPassword> tAccountPasswords =
                tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
        if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
            return isSuccess;
        }
        TAccountPassword tAccountPassword = tAccountPasswords.get(0);
        
        String newPwd = CustomerUtil.getRandomStringByLen(6);
        log.info("newPwd :{}",newPwd);
        //String newPwd=UUID.randomUUID().toString().replace("-", "");
        String salt=AccountBusiness.getSalt();
        if (MD5.sign(newPwd, tAccountPassword.getSalt(), "utf-8").equals(
                tAccountPassword.getPassword())) {
            newPwd = CustomerUtil.getRandomStringByLen(6);
        }
        tAccountPassword.setUpdateTime(new Date());
        tAccountPassword.setSalt(salt);
        tAccountPassword.setPassword(MD5.sign(newPwd, salt, "utf-8"));
        int count = tAccountPasswordMapper.updateByPrimaryKeySelective(tAccountPassword);
        if (count > 0) {
            /**
             * TODO send mobile msg
             */
            isSuccess = true;
            try{
                smsPushProvider.sendMsg("APP_NEW_TRADE", mobile, "您的密码是:"+newPwd);
            }catch(Exception e){
                log.error(" resetPassword  Exception:{} ",e);
            }
        }
        
        return isSuccess;
    }
    
    /**
     * 重置或修改密码
     * @param userId
     * @param password
     * @return
     */
    public boolean resetOrUpdatePassword(String userId, String password) {
        boolean isSuccess = false;
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(example);
        if (tAccounts == null || tAccounts.size() <= 0) {
            return isSuccess;
        }
        TAccount tAccount = tAccounts.get(0);
        TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
        tAccountPasswordExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
        List<TAccountPassword> tAccountPasswords = tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
        String salt=AccountBusiness.getSalt();
        Date date = new Date();
        int count = 0;
        if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
            TAccountPassword tAccountPassword = new TAccountPassword();
            tAccountPassword.setAccountId(tAccount.getAccountId());
            tAccountPassword.setUserId(userId);
            tAccountPassword.setCreateTime(new Date());
            tAccountPassword.setPassword(MD5.sign(password, salt, "utf-8"));
            tAccountPassword.setUpdateTime(date);
            tAccountPassword.setSalt(salt);
            tAccountPassword.setErrorCount(0);
            tAccountPassword.setLimitExpireTime(date);
            count = tAccountPasswordMapper.insert(tAccountPassword);
        }else{
            TAccountPassword tAccountPassword = tAccountPasswords.get(0);
            tAccountPassword.setUpdateTime(date);
            tAccountPassword.setSalt(salt);
            tAccountPassword.setPassword(MD5.sign(password, salt, "utf-8"));
            tAccountPassword.setErrorCount(0);
            tAccountPassword.setLimitExpireTime(date);
            count = tAccountPasswordMapper.updateByPrimaryKeySelective(tAccountPassword);
        }
        return count > 0;
    }
    
    /**
     * 密码重置
     * 
     * @param userId
     * @param password
     * @return
     */
    public boolean modifyPassword(String userId,String password) {
        boolean isSuccess = false;
        if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if (password == null || password.trim().length() <6) {
            throw new IllegalArgumentException("parameter newPassword is invalid");
        }
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(example);
        if (tAccounts == null || tAccounts.size() <= 0) {
            return isSuccess;
        }
        TAccount tAccount = tAccounts.get(0);
        TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
        tAccountPasswordExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
        List<TAccountPassword> tAccountPasswords =
                tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
        if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
            return isSuccess;
        }
        TAccountPassword tAccountPassword = tAccountPasswords.get(0);
        String newPwd=password;
        String salt=AccountBusiness.getSalt();
        if (MD5.sign(newPwd, tAccountPassword.getSalt(), "utf-8").equals(
                tAccountPassword.getPassword())) {
            return isSuccess;
        }
        tAccountPassword.setUpdateTime(new Date());
        tAccountPassword.setSalt(salt);
        tAccountPassword.setPassword(MD5.sign(newPwd, salt, "utf-8"));
        tAccountPasswordMapper.updateByPrimaryKeySelective(tAccountPassword);
        isSuccess=true;
        return isSuccess;
    }
    
    public void checkModifyPwdParam(String userId, String oldPassword, String newPassword) {
        if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
        if (oldPassword == null || oldPassword.trim().length() < 6) {
            throw new IllegalArgumentException("parameter oldPassword is invalid");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new IllegalArgumentException("parameter newPassword is invalid");
        }
        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("parameter newPassword is invalid");
        }
    }
    
    public static final String getSalt(){
        return UUID.randomUUID().toString();
    }
    
    //按业务类型+用户ID查询未销户账户
    public TAccount qryAccountByUserId( String userId){
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId).andIsDelEqualTo(0);
        List<TAccount> list = tAccountMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }else{
            return list.get(0);
        }
    }
    
    public Balance queryBalance(String userId) {

        if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }

        Balance resBalance = new Balance();
        resBalance.setUserId(userId);
        List<TAccountBalance> balanceList = this.qryRealTimeBalance(userId);
        
        Long withdrawAmount=withdrawBussiness.queryCanWithdrawAmount(userId);
        if (CollectionUtils.isNotEmpty(balanceList)) {
            long total=0L;
            for (TAccountBalance balance : balanceList) {
                if (Constants.BALANCE_TYPE_AVALIABLE.equals(balance.getBalanceType())) {
                    resBalance.setAvailableBalance(balance.getAmount());
                    if(balance.getAmount()!=null){
                        total+=balance.getAmount();
                    }
                   
                } else if (Constants.BALANCE_TYPE_FREEZON.equals(balance.getBalanceType())) {
                    resBalance.setFrozenBalance(balance.getAmount());
                    if(balance.getAmount()!=null){
                        total+=balance.getAmount();
                    }
                } else if (Constants.BALANCE_TYPE_LOAN.equals(balance.getBalanceType())) {
                    resBalance.setLoanBalance(balance.getAmount());
                } else if (Constants.BALANCE_TYPE_UNPAY.equals(balance.getBalanceType())) {
                    resBalance.setOtherUnpaid(balance.getAmount());
                    if(balance.getAmount()!=null){
                        total+=balance.getAmount();
                    }
                }
            }
            if(null == resBalance.getAvailableBalance()){
            	resBalance.setAvailableBalance(0L);
            }
            if(null == resBalance.getFrozenBalance()){
            	resBalance.setFrozenBalance(0L);
            }
            if(null == resBalance.getLoanBalance()){
            	resBalance.setLoanBalance(0L);
            }
            if(null == resBalance.getOtherUnpaid()){
            	resBalance.setOtherUnpaid(0L);
            }
            
            resBalance.setTotalBalance(total);

        } else {
            /**throw new IllegalArgumentException("用户ID=[" + userId + "]余额记录不存在");**/
           /** resBalance.setAvailableBalance(0L);
            resBalance.setFrozenBalance(0L);
            resBalance.setLoanBalance(0L);
            resBalance.setOtherUnpaid(0L);
            resBalance.setTotalBalance(0L);**/
            return null;
        }

        long canWthdraw=0L;
        if(withdrawAmount!=null&&withdrawAmount.longValue()>0){
            canWthdraw=withdrawAmount;
        }
        resBalance.setCanWithdraw(canWthdraw);
        return resBalance;
    }

    public List<BalanceAndSignedStatus> queryBalanceListOld(List<String> userIds,
            Boolean ignoreZeroAmount) {
        List<BalanceAndSignedStatus> balanceAndSignedStatusList =
                new ArrayList<BalanceAndSignedStatus>();
        for (String userId : userIds) {
            BalanceAndSignedStatus balanceAndSignedStatus = queryBalanceAndSignedStatus(userId);

            if (ignoreZeroAmount) {
                if (0 != balanceAndSignedStatus.getBalance().getTotalBalance()) {
                    balanceAndSignedStatusList.add(balanceAndSignedStatus);
                }
            } else {
                balanceAndSignedStatusList.add(balanceAndSignedStatus);
            }
        }
        return balanceAndSignedStatusList;
    }
    
    
    
    public List<BalanceAndSignedStatus> queryBalanceList(List<String> userIds,
            Boolean ignoreZeroAmount) {
        return queryUsersBalanceAndSignedStatus(userIds,ignoreZeroAmount);
    }
    
    
    
  public List<BalanceAndSignedStatus> queryUsersBalanceAndSignedStatus(List<String> userIds,Boolean ignoreZeroAmount) {
        
        if (userIds == null || userIds.size() <= 0) {
            throw new IllegalArgumentException("parameter userIds can not be blank");
        }
        
        Balance resBalance = null;
        
        Map<String, List<TAccountBalance>> balanceLists = this.qryUsersRealTimeBalance(userIds);
        Map<Long, Long> usersWitdrawAmountMap =withdrawBussiness.queryUsersCanWithdrawAmount(userIds);
        
        List<BalanceAndSignedStatus> balanceAndSignedStatuss=new ArrayList<BalanceAndSignedStatus>();
        for (int i = 0; i < userIds.size(); i++) {
            String userId=userIds.get(i);
            BalanceAndSignedStatus balanceAndSignedStatus = new BalanceAndSignedStatus();
            List<TAccountBalance> balanceList=balanceLists.get(userId);
            
            if (balanceList != null) {
                resBalance = new Balance();
                resBalance.setUserId(userId);

                for (TAccountBalance balance : balanceList) {
                    if (Constants.BALANCE_TYPE_AVALIABLE.equals(balance.getBalanceType())) {
                        resBalance.setAvailableBalance(balance.getAmount());
                    } else if (Constants.BALANCE_TYPE_FREEZON.equals(balance.getBalanceType())) {
                        resBalance.setFrozenBalance(balance.getAmount());
                    } else if (Constants.BALANCE_TYPE_LOAN.equals(balance.getBalanceType())) {
                        resBalance.setLoanBalance(balance.getAmount());
                    } else if (Constants.BALANCE_TYPE_UNPAY.equals(balance.getBalanceType())) {
                        resBalance.setOtherUnpaid(balance.getAmount());
                    }
                }

                if (null == resBalance.getAvailableBalance()) {
                    resBalance.setAvailableBalance(0L);
                }
                if (null == resBalance.getFrozenBalance()) {
                    resBalance.setFrozenBalance(0L);
                }
                if (null == resBalance.getOtherUnpaid()) {
                    resBalance.setOtherUnpaid(0L);
                }
                if (null == resBalance.getLoanBalance()) {
                    resBalance.setLoanBalance(0L);
                }
                resBalance.setTotalBalance(resBalance.getAvailableBalance()
                        + resBalance.getFrozenBalance() + resBalance.getOtherUnpaid());

                balanceAndSignedStatus.setBalance(resBalance);
                balanceAndSignedStatus.setIsSigned(false);
                TAccountExample tAccountExample = new TAccountExample();
                tAccountExample.createCriteria().andUserIdEqualTo(userId);
                List<TAccount> tAccountList = tAccountMapper.selectByExample(tAccountExample);
                if (CollectionUtils.isNotEmpty(tAccountList)) {
                    TAccount tAccount = tAccountList.get(0);
                    Integer isSign = tAccount.getIsSign();

                    String bankNo = tAccount.getSignChannel();
                    balanceAndSignedStatus.setSignBankNo(bankNo);
                    balanceAndSignedStatus.setSignAccountId(tAccount.getSignAccountId());
                    balanceAndSignedStatus.setAccountId(tAccount.getAccountId());

                    TBankInfoExample tBankInfoExample = new TBankInfoExample();
                    tBankInfoExample.createCriteria().andBankNoEqualTo(bankNo);
                    List<TBankInfo> tBankInfoList =
                            tBankInfoMapper.selectByExample(tBankInfoExample);

                    if (CollectionUtils.isNotEmpty(tBankInfoList)) {
                        balanceAndSignedStatus.setSignChannel(tBankInfoList.get(0).getBankName());
                    }

                    if (1 == isSign) {
                        balanceAndSignedStatus.setIsSigned(true);
                    } else {
                        balanceAndSignedStatus.setIsSigned(false);
                    }
                }

                long canWthdraw = 0L;
                Long withdrawAmount= usersWitdrawAmountMap.get(userId);
                if (withdrawAmount != null && withdrawAmount.longValue() > 0) {
                    canWthdraw = withdrawAmount;
                }
                resBalance.setCanWithdraw(canWthdraw);
            }else{
              //  throw new IllegalArgumentException("用户ID=[" + userId + "]余额记录不存在");
                resBalance = new Balance();
                resBalance.setUserId(userId);
                resBalance.setAvailableBalance(0L);
                resBalance.setFrozenBalance(0L);
                resBalance.setLoanBalance(0L);
                resBalance.setOtherUnpaid(0L);
                resBalance.setTotalBalance(0L);
                balanceAndSignedStatus.setBalance(resBalance);
                balanceAndSignedStatus.setIsSigned(false);
                balanceAndSignedStatus.setSignAccountId("");
                balanceAndSignedStatus.setSignBankNo("");
                balanceAndSignedStatus.setSignChannel("");
            }
            
            if (ignoreZeroAmount) {
                if (0 != balanceAndSignedStatus.getBalance().getTotalBalance()) {
                    balanceAndSignedStatuss.add(balanceAndSignedStatus);
                }
            } else {
                balanceAndSignedStatuss.add(balanceAndSignedStatus);
            }

        }
        
        
        return balanceAndSignedStatuss;
    }
    
    
    
    
    
    
    
    public BalanceAndSignedStatus queryBalanceAndSignedStatus(String userId) {
    	
    	if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    	
    	BalanceAndSignedStatus balanceAndSignedStatus = new BalanceAndSignedStatus();
    	Balance resBalance = null;
    	
        List<TAccountBalance> balanceList = this.qryRealTimeBalance(userId);
        if(CollectionUtils.isNotEmpty(balanceList)){
        	resBalance = new Balance();
        	resBalance.setUserId(userId);
        	Long withdrawAmount=withdrawBussiness.queryCanWithdrawAmount(userId);
            for(TAccountBalance balance : balanceList){
                if(Constants.BALANCE_TYPE_AVALIABLE.equals( balance.getBalanceType())){
                	resBalance.setAvailableBalance(balance.getAmount());
                }else if(Constants.BALANCE_TYPE_FREEZON.equals( balance.getBalanceType())){
                	resBalance.setFrozenBalance(balance.getAmount());
                }else if(Constants.BALANCE_TYPE_LOAN.equals( balance.getBalanceType())){
                	resBalance.setLoanBalance(balance.getAmount());
                }else if(Constants.BALANCE_TYPE_UNPAY.equals( balance.getBalanceType())){
                	resBalance.setOtherUnpaid(balance.getAmount());
                }
            }
            
            if(null == resBalance.getAvailableBalance()){
            	resBalance.setAvailableBalance(0L);
            }
            if(null == resBalance.getFrozenBalance()){
            	resBalance.setFrozenBalance(0L);
            }
            if(null == resBalance.getOtherUnpaid()){
            	resBalance.setOtherUnpaid(0L);
            }
            if(null == resBalance.getLoanBalance()){
            	resBalance.setLoanBalance(0L);
            }
            resBalance.setTotalBalance(resBalance.getAvailableBalance()+resBalance.getFrozenBalance()+resBalance.getOtherUnpaid());
           
            balanceAndSignedStatus.setBalance(resBalance);
            balanceAndSignedStatus.setIsSigned(false);
            TAccountExample tAccountExample = new TAccountExample();
            tAccountExample.createCriteria().andUserIdEqualTo(userId);
            List<TAccount> tAccountList = tAccountMapper.selectByExample(tAccountExample);
            if(CollectionUtils.isNotEmpty(tAccountList)){
            	TAccount tAccount = tAccountList.get(0);
            	Integer isSign = tAccount.getIsSign();
            	
            	String bankNo = tAccount.getSignChannel();
            	balanceAndSignedStatus.setSignBankNo(bankNo);
            	balanceAndSignedStatus.setSignAccountId(tAccount.getSignAccountId());
            	balanceAndSignedStatus.setAccountId(tAccount.getAccountId());
            	
            	TBankInfoExample tBankInfoExample = new TBankInfoExample();
            	tBankInfoExample.createCriteria().andBankNoEqualTo(bankNo);
            	List<TBankInfo> tBankInfoList = tBankInfoMapper.selectByExample(tBankInfoExample);
            	
            	if(CollectionUtils.isNotEmpty(tBankInfoList)){
            		balanceAndSignedStatus.setSignChannel(tBankInfoList.get(0).getBankName());
            	}
            	
            	if(1 == isSign){
            		balanceAndSignedStatus.setIsSigned(true);
            	}else{
            		balanceAndSignedStatus.setIsSigned(false);
            	}
            }
            
            long canWthdraw=0L;
            if(withdrawAmount!=null&&withdrawAmount.longValue()>0){
                canWthdraw=withdrawAmount;
            }
            resBalance.setCanWithdraw(canWthdraw);
        }else{
          //  throw new IllegalArgumentException("用户ID=[" + userId + "]余额记录不存在");
            resBalance = new Balance();
            resBalance.setUserId(userId);
            resBalance.setAvailableBalance(0L);
            resBalance.setFrozenBalance(0L);
            resBalance.setLoanBalance(0L);
            resBalance.setOtherUnpaid(0L);
            resBalance.setTotalBalance(0L);
            balanceAndSignedStatus.setBalance(resBalance);
            balanceAndSignedStatus.setIsSigned(false);
            balanceAndSignedStatus.setSignAccountId("");
            balanceAndSignedStatus.setSignBankNo("");
            balanceAndSignedStatus.setSignChannel("");
        }
    	
        return balanceAndSignedStatus;
    }
    
    public BalanceSummary queryBalanceSummary(String userId, Date startTime, Date endTime){
    	if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    	if (startTime == null) {
    		throw new IllegalArgumentException("parameter startTime can not be blank");
    	}
    	if (endTime == null) {
    		throw new IllegalArgumentException("parameter endTime can not be blank");
    	}
    	
    	BalanceSummary balanceSummary = null;
    	
		if(DateUtils.isSameDay (startTime,endTime)){
			Calendar c = Calendar.getInstance();
			c.setTime(endTime);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DAY_OF_MONTH, 1);
			endTime = c.getTime();
		}
    	
    	TAccountCashierLogExample tAccountCashierLogExample = new TAccountCashierLogExample();
    	tAccountCashierLogExample.createCriteria().andUserIdEqualTo(userId).andCreateTimeBetween(startTime, endTime);
    	tAccountCashierLogExample.setOrderByClause("create_time asc");
    	List<TAccountCashierLog> tAccountCashierLogList = tAccountCashierLogMapper.selectByExample(tAccountCashierLogExample);
    	if(CollectionUtils.isNotEmpty(tAccountCashierLogList)){
    		balanceSummary = new BalanceSummary();
    		balanceSummary.setUserId(userId);
    		
    		TAccountCashierLog tAccountCashierLogFirst = tAccountCashierLogList.get(0);
    		balanceSummary.setBeginBalance(tAccountCashierLogFirst.getChangeBefore());
    		
    		//取到符合时间范围的最后一个Item
    		int lastItemIndex = tAccountCashierLogList.size()-1;
    		TAccountCashierLog tAccountCashierLogLast = null;
    		
    		while(lastItemIndex>=0){
    			tAccountCashierLogLast = tAccountCashierLogList.get(lastItemIndex);
    			if(DateUtils.isSameDay (tAccountCashierLogLast.getCreateTime(),endTime)){
    				lastItemIndex--;
    			}else{
    				break;
    			}
    		}
    		
    		balanceSummary.setEndBalance(tAccountCashierLogLast.getChangeBefore()+tAccountCashierLogLast.getChangeAmount());
    		
    		//累加,出金,入金,当期收入
    		Long inMoney = 0L;
    		Long outMoney = 0L;
    		Long income = 0L;
    		for(TAccountCashierLog tAccountCashierLog:tAccountCashierLogList){
    			if(!DateUtils.isSameDay (tAccountCashierLog.getCreateTime(),endTime)){
    				income = income+tAccountCashierLog.getChangeAmount();
    				if(Constants.ORDER_TYPE_RECHARGE.equals(tAccountCashierLog.getOrderType())){
    					inMoney = inMoney+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.ORDER_TYPE_WITHDRAW.equals(tAccountCashierLog.getOrderType())){
    					outMoney = outMoney+tAccountCashierLog.getChangeAmount();
    				}
    			}
    		}
    		balanceSummary.setInMoney(inMoney==null?0:inMoney);
    		balanceSummary.setOutMoney(outMoney==null?0:outMoney);
    		balanceSummary.setIncome(income==null?0:income);
    	}
    	
    	return balanceSummary;
    }
    
    public List<BalanceSummary> queryBalanceSumaryList(List<String> userIds, Date startTime, Date endTime) {
    	List<BalanceSummary> balanceSummaryList = new ArrayList<BalanceSummary>();
    	
    	if (userIds == null || userIds.size() <= 0) {
            throw new IllegalArgumentException("parameter userIds can not be blank");
        }
    	if (startTime == null) {
    		throw new IllegalArgumentException("parameter startTime can not be blank");
    	}
    	if (endTime == null) {
    		throw new IllegalArgumentException("parameter endTime can not be blank");
    	}
    	
    	for(String userId:userIds){
    		BalanceSummary balanceSummary = null;
    		try {
        		balanceSummary = queryBalanceSummary(userId, startTime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if(null != balanceSummary){
    			balanceSummaryList.add(balanceSummary);
    		}
    	}
    	
    	return balanceSummaryList;
    }
    
    public FeeAndInterest queryIncome(String userId, Date startTime, Date endTime) {
    	if (userId == null || userId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    	if (startTime == null) {
    		throw new IllegalArgumentException("parameter startTime can not be blank");
    	}
    	if (endTime == null) {
    		throw new IllegalArgumentException("parameter endTime can not be blank");
    	}
    	
    	FeeAndInterest feeAndInterest = null;
    	
    	if(DateUtils.isSameDay (startTime,endTime)){
			Calendar c = Calendar.getInstance();
			c.setTime(endTime);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DAY_OF_MONTH, 1);
			endTime = c.getTime();
		}
    	
    	TAccountCashierLogExample tAccountCashierLogExample = new TAccountCashierLogExample();
    	tAccountCashierLogExample.createCriteria().andUserIdEqualTo(userId).andCreateTimeBetween(startTime, endTime);
    	tAccountCashierLogExample.setOrderByClause("create_time asc");
    	List<TAccountCashierLog> tAccountCashierLogList = tAccountCashierLogMapper.selectByExample(tAccountCashierLogExample);
    	if(CollectionUtils.isNotEmpty(tAccountCashierLogList)){
    		feeAndInterest = new FeeAndInterest();
    		
    		//累加,交易手续费,融资手续费,融货手续费,融资利息,融货利息
    		Long tradeFee = 0L;
    		Long loanFundFee = 0L;
    		Long loanSpotFee = 0L;
    		Long loanFundInterest = 0L;
    		Long loanSpotInterest = 0L;
    		for(TAccountCashierLog tAccountCashierLog:tAccountCashierLogList){
    			if(!DateUtils.isSameDay (tAccountCashierLog.getCreateTime(),endTime)){
    				if(Constants.FEE_TYPE_BUYTRADE.equals(tAccountCashierLog.getFeeItem())){
    					tradeFee = tradeFee+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.FEE_TYPE_SELLTRADE.equals(tAccountCashierLog.getFeeItem())){
    					tradeFee = tradeFee+tAccountCashierLog.getChangeAmount();
    				}
    				
    				if(Constants.FEE_TYPE_LOAN_POUNDAGE.equals(tAccountCashierLog.getFeeItem())){
    					loanFundFee = loanFundFee+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.FEE_TYPE_MARGIN_POUNDAGE.equals(tAccountCashierLog.getFeeItem())){
    					loanSpotFee = loanSpotFee+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.FEE_TYPE_LOANINTEREST.equals(tAccountCashierLog.getFeeItem())){
    					loanFundInterest = loanFundInterest+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.FEE_TYPE_MARGININTEREST.equals(tAccountCashierLog.getFeeItem())){
    					loanSpotInterest = loanSpotInterest+tAccountCashierLog.getChangeAmount();
    				}
    			}
    		}
    		feeAndInterest.setTradeFee(tradeFee);
    		feeAndInterest.setLoanFundFee(loanFundFee);
    		feeAndInterest.setLoanSpotFee(loanSpotFee);
    		feeAndInterest.setLoanFundInterest(loanFundInterest);
    		feeAndInterest.setLoanSpotInterest(loanSpotInterest);
    		feeAndInterest.setTotal(tradeFee+loanFundFee+loanSpotFee+loanFundInterest+loanSpotInterest);
    	}
    	
    	return feeAndInterest;
    }
    
    public List<OrgBalanceSummary> queryBalanceSumaryListByOrgList(List<String> orgList, Date startTime, Date endTime) {
    	
    	List<OrgBalanceSummary> OrgBalanceSummaryList = new ArrayList<OrgBalanceSummary>();
    	
    	if (orgList == null || orgList.size() <= 0) {
            throw new IllegalArgumentException("parameter orgList can not be blank");
        }
    	if (startTime == null) {
    		throw new IllegalArgumentException("parameter startTime can not be blank");
    	}
    	if (endTime == null) {
    		throw new IllegalArgumentException("parameter endTime can not be blank");
    	}
    	
    	for(String orgId:orgList){
    		OrgBalanceSummary orgBalanceSummary = null;
    		try {
    			orgBalanceSummary = queryOrgBalanceSummary(orgId, startTime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if(null != orgBalanceSummary){
    			OrgBalanceSummaryList.add(orgBalanceSummary);
    		}
    	}
    	
    	return OrgBalanceSummaryList;
    }
    
    public OrgBalanceSummary queryOrgBalanceSummary(String orgId, Date startTime, Date endTime){
    	if (orgId == null || orgId.trim().length() <= 0) {
            throw new IllegalArgumentException("parameter orgId can not be blank");
        }
    	if (startTime == null) {
    		throw new IllegalArgumentException("parameter startTime can not be blank");
    	}
    	if (endTime == null) {
    		throw new IllegalArgumentException("parameter endTime can not be blank");
    	}
    	
    	OrgBalanceSummary orgBalanceSummary = null;
    	
		if(DateUtils.isSameDay (startTime,endTime)){
			Calendar c = Calendar.getInstance();
			c.setTime(endTime);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DAY_OF_MONTH, 1);
			endTime = c.getTime();
		}
    	
    	TAccountCashierLogExample tAccountCashierLogExample = new TAccountCashierLogExample();
    	tAccountCashierLogExample.createCriteria().andOrgIdEqualTo(orgId).andCreateTimeBetween(startTime, endTime);
    	tAccountCashierLogExample.setOrderByClause("create_time asc");
    	List<TAccountCashierLog> tAccountCashierLogList = tAccountCashierLogMapper.selectByExample(tAccountCashierLogExample);
    	if(CollectionUtils.isNotEmpty(tAccountCashierLogList)){
    		orgBalanceSummary = new OrgBalanceSummary();
    		TAccountCashierLog tAccountCashierLogFirst = tAccountCashierLogList.get(0);
    		orgBalanceSummary.setBeginBalance(tAccountCashierLogFirst.getChangeBefore());
    		orgBalanceSummary.setOrgId(tAccountCashierLogFirst.getOrgId());
    		
    		//取到符合时间范围的最后一个Item
    		int lastItemIndex = tAccountCashierLogList.size()-1;
    		TAccountCashierLog tAccountCashierLogLast = null;
    		
    		while(lastItemIndex>=0){
    			tAccountCashierLogLast = tAccountCashierLogList.get(lastItemIndex);
    			if(DateUtils.isSameDay (tAccountCashierLogLast.getCreateTime(),endTime)){
    				lastItemIndex--;
    			}else{
    				break;
    			}
    		}
    		
    		orgBalanceSummary.setEndBalance(tAccountCashierLogLast.getChangeBefore()+tAccountCashierLogLast.getChangeAmount());
    		
    		//累加,出金,入金,当期收入
    		Long inMoney = 0L;
    		Long outMoney = 0L;
    		Long income = 0L;
    		for(TAccountCashierLog tAccountCashierLog:tAccountCashierLogList){
    			if(!DateUtils.isSameDay (tAccountCashierLog.getCreateTime(),endTime)){
    				income = income+tAccountCashierLog.getChangeAmount();
    				if(Constants.ORDER_TYPE_RECHARGE.equals(tAccountCashierLog.getOrderType())){
    					inMoney = inMoney+tAccountCashierLog.getChangeAmount();
    				}
    				if(Constants.ORDER_TYPE_WITHDRAW.equals(tAccountCashierLog.getOrderType())){
    					outMoney = outMoney+tAccountCashierLog.getChangeAmount();
    				}
    			}
    		}
    		orgBalanceSummary.setInMoney(inMoney);
    		orgBalanceSummary.setOutMoney(outMoney);
    		orgBalanceSummary.setIncome(income);
    	}
    	
    	return orgBalanceSummary;
    }
    
    /**
     * 应用在途账户余额变更
     * @param item
     */
    @Transactional
    public boolean applyBalanceOnTheWay(TAccountBalanceOntheway item){
        Date date = new Date();
        TAccountBalanceExample example = new TAccountBalanceExample();
        example.createCriteria().andAccountIdEqualTo(item.getAccountId()).andBalanceTypeEqualTo(item.getBalanceType());
        TAccountBalance accountBalance = this.qryFinalBalance(example).get(0);
        
        //更新账户余额,使用乐观锁
        accountBalance.setVersion(accountBalance.getVersion() + 1);
        accountBalance.setAmount(accountBalance.getAmount() + item.getAmount());
        accountBalance.setUpdateTime(date);
        TAccountBalanceExample example2 = new TAccountBalanceExample();
        example2.createCriteria().andAccountBalanceIdEqualTo(accountBalance.getAccountBalanceId()).andVersionEqualTo(accountBalance.getVersion()-1);
        int count = tAccountBalanceMapper.updateByExample(accountBalance, example2);
        if(count > 0){
            //更新在途账户状态
            item.setIsHandle(1);
            item.setUpdateTime(date);
            tAccountBalanceOnthewayMapper.updateByPrimaryKey(item);
            return true;
        }else{//更新余额失败
            return false;
        }
    }
    
    /**
     * 最终余额查询（不包括在途）
     * @param example
     * @return
     */
    public List<TAccountBalance> qryFinalBalance(TAccountBalanceExample example){
        return tAccountBalanceMapper.selectByExample(example);
    } 
    
    /**
     * 实时余额查询（包括在途）
     * @param accountId
     * @param balanceTypeList
     * @return
     */
    public List<TAccountBalance> qryRealTimeBalance(Long accountId, List<String> balanceTypeList){
        TAccountBalanceExample example = new TAccountBalanceExample();
        example.createCriteria().andAccountIdEqualTo(accountId).andBalanceTypeIn(balanceTypeList);
        
        TAccountBalanceOnthewayExample onTheWayExample = new TAccountBalanceOnthewayExample();
        onTheWayExample.createCriteria().andIsHandleEqualTo(0).andAccountIdEqualTo(accountId).andBalanceTypeIn(balanceTypeList);
      
        return mergeBalanceOneTheWay(example, onTheWayExample);
    }
    
    /**
     * 实时余额查询（包括在途）
     * @param userId
     * @return
     */
    public List<TAccountBalance> qryRealTimeBalance(String userId){
        TAccountBalanceExample example = new TAccountBalanceExample();
        example.createCriteria().andUserIdEqualTo(userId);
        
        TAccountBalanceOnthewayExample onTheWayExample = new TAccountBalanceOnthewayExample();
        onTheWayExample.createCriteria().andIsHandleEqualTo(0).andUserIdEqualTo(userId);
      
        return mergeBalanceOneTheWay(example, onTheWayExample);
    }
    
    
    /**
     * 实时余额查询（包括在途）
     * @param userId
     * @return
     */
    public Map<String, List<TAccountBalance>> qryUsersRealTimeBalance(List<String> userIds){
        TAccountBalanceExample example = new TAccountBalanceExample();
        example.createCriteria().andUserIdIn(userIds);
        
        TAccountBalanceOnthewayExample onTheWayExample = new TAccountBalanceOnthewayExample();
        onTheWayExample.createCriteria().andIsHandleEqualTo(0).andUserIdIn(userIds);
      
        return mergeUsersBalanceOneTheWay(example, onTheWayExample);
    }
    
    
    
    
    
    /**
     * 合并余额及在途余额
     * @param example
     * @param onTheWayExample
     * @return
     */
    public Map<String,List<TAccountBalance>> mergeUsersBalanceOneTheWay(TAccountBalanceExample example, TAccountBalanceOnthewayExample onTheWayExample){
        List<String> userIds=new ArrayList<String>();
        //按条件查余额表
        List<TAccountBalance> list = this.tAccountBalanceMapper.selectByExample(example);
        Map<String,List<TAccountBalance>>  balanceMap=new HashMap<String,List<TAccountBalance>>();
        for (int i = 0; i < list.size(); i++) {
            TAccountBalance tAccountBalance=list.get(i);
            String userId=tAccountBalance.getUserId();
            List<TAccountBalance> accountBalances= balanceMap.get(userId);
            if(accountBalances==null){
                accountBalances=new ArrayList<TAccountBalance>();
            }
            accountBalances.add(tAccountBalance);
            userIds.add(userId);
            balanceMap.put(userId, accountBalances);
        }
        
        
        
        //在途余额列表
        List<TAccountBalanceOntheway> onTheWayList = tAccountBalanceOnthewayMapper.selectByExample(onTheWayExample);
        Map<String,Long>  onwaybalanceMap=new HashMap<String,Long>();
        for (int i = 0; i < onTheWayList.size(); i++) {
            TAccountBalanceOntheway tAccountBalanceOntheway = onTheWayList.get(i);
            String userId = tAccountBalanceOntheway.getUserId();
            Long onwayBalanceAmount =
                    onwaybalanceMap.get(userId + "#" + tAccountBalanceOntheway.getBalanceType());
            if (onwayBalanceAmount == null) {
                onwayBalanceAmount = 0L;
            }
            onwayBalanceAmount = onwayBalanceAmount + tAccountBalanceOntheway.getAmount();
            onwaybalanceMap.put(userId + "#" + tAccountBalanceOntheway.getBalanceType(),
                    onwayBalanceAmount);
        }
        
        
        
        for (int i = 0; i < userIds.size(); i++) {
            String userId=userIds.get(i);
            List<TAccountBalance> tAccountBalances=balanceMap.get(userId);
            
           // Map<String, TAccountBalance> accountIdAndBalnceType2Balance = new HashMap<String, TAccountBalance>();
            if(CollectionUtils.isNotEmpty(tAccountBalances)){
                for(TAccountBalance tAccountBalance : tAccountBalances){
                    Long onwayBalanceAmount=onwaybalanceMap.get(userId + "#" + tAccountBalance.getBalanceType());
                    if(onwayBalanceAmount!=null){
                        tAccountBalance.setAmount(tAccountBalance.getAmount()+onwayBalanceAmount);
                    }
                   // accountIdAndBalnceType2Balance.put(tAccountBalance.getAccountId() + "$" + tAccountBalance.getBalanceType(), tAccountBalance);
                }
            }

          /**  List<TAccountBalanceOntheway> oAccountBalances=onwaybalanceMap.get(userId);
            
            if(CollectionUtils.isNotEmpty(oAccountBalances)){
                for(TAccountBalanceOntheway tAccountBalanceOntheway : oAccountBalances){
                    TAccountBalance tAccountBalance = accountIdAndBalnceType2Balance.get(tAccountBalanceOntheway.getAccountId() + "$" + tAccountBalanceOntheway.getBalanceType());
                    if(tAccountBalance != null){
                        tAccountBalance.setAmount(tAccountBalance.getAmount() + tAccountBalanceOntheway.getAmount());
                        tAccountBalance.setVersion(0L);//version设置为0，防止误更新
                    }
                }
            }**/
            
        }
        
        
        
        
        return balanceMap;
    }
    
    
    
    
    
    
    
    
    
    /**
     * 合并余额及在途余额
     * @param example
     * @param onTheWayExample
     * @return
     */
    public List<TAccountBalance> mergeBalanceOneTheWay(TAccountBalanceExample example, TAccountBalanceOnthewayExample onTheWayExample){
        //按条件查余额表
        List<TAccountBalance> list = this.tAccountBalanceMapper.selectByExample(example);
        Map<String, TAccountBalance> accountIdAndBalnceType2Balance = new HashMap<String, TAccountBalance>();
        if(CollectionUtils.isNotEmpty(list)){
            for(TAccountBalance tAccountBalance : list){
                accountIdAndBalnceType2Balance.put(tAccountBalance.getAccountId() + "$" + tAccountBalance.getBalanceType(), tAccountBalance);
            }
        }else{
            return list;
        }
        
        //在途余额列表
        List<TAccountBalanceOntheway> onTheWayList = tAccountBalanceOnthewayMapper.selectByExample(onTheWayExample);
        if(CollectionUtils.isNotEmpty(onTheWayList)){
            for(TAccountBalanceOntheway tAccountBalanceOntheway : onTheWayList){
                TAccountBalance tAccountBalance = accountIdAndBalnceType2Balance.get(tAccountBalanceOntheway.getAccountId() + "$" + tAccountBalanceOntheway.getBalanceType());
                if(tAccountBalance != null){
                    tAccountBalance.setAmount(tAccountBalance.getAmount() + tAccountBalanceOntheway.getAmount());
                    tAccountBalance.setVersion(0L);//version设置为0，防止误更新
                }
            }
        }
        return list;
    }
    
    /**
     * 查询交易是否已存在
     * @param orderType
     * @param orderId
     * @return
     */
    public Boolean checkTransIsNotExists(String orderType, String orderId){
        TAccountCashierOrderExample example = new TAccountCashierOrderExample();
        example.createCriteria().andOrderTypeEqualTo(orderType).andOrderIdEqualTo(orderId);
        List<TAccountCashierOrder> list = tAccountCashierOrderMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list);
    }
    
}
