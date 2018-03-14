package com.baibei.accountservice.paycenter.bussiness.ch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.account.dto.exception.BalanceNotEnoughException;
import com.baibei.account.dto.notify.PaySuccessNotify;
import com.baibei.account.dto.request.QueryTransferRecordsRequest;
import com.baibei.account.dto.request.TransferRequest;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.dto.response.TransferRecord;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.config.DynamicConfig;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountBalanceSnapshotMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TAccountPasswordMapper;
import com.baibei.accountservice.dao.TPayLimitMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountBalanceSnapshot;
import com.baibei.accountservice.model.TAccountBalanceSnapshotExample;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TAccountPassword;
import com.baibei.accountservice.model.TAccountPasswordExample;
import com.baibei.accountservice.model.TAppConfig;
import com.baibei.accountservice.model.TPayLimit;
import com.baibei.accountservice.model.TPayLimitExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.bussiness.AppConfigBusiness;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.dto.base.BaseResponse;
import com.baibei.accountservice.paycenter.dto.request.WithdrawBalanceRequest;
import com.baibei.accountservice.paycenter.dto.response.RechargeResponse;
import com.baibei.accountservice.paycenter.dto.response.WithdrawResponse;
import com.baibei.accountservice.paycenter.exception.PasswordException;
import com.baibei.accountservice.paycenter.exception.PayException;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.utill.PayRestfulUtil;
import com.baibei.accountservice.paycenter.vo.response.WithdrawNotify;
import com.baibei.accountservice.rocketmq.RocketMQUtils;
import com.baibei.accountservice.util.IDGenerator;
import com.baibei.accountservice.util.MD5;
import com.baibei.repository.dto.base.UserInventoryAccountDTO;
import com.baibei.repository.request.query.QueryUserInventoryAcctListByIDsRequest;
import com.baibei.repository.service.query.UserInventoryAccountQueryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class WithdrawBussiness {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawBussiness.class);
   /* @Autowired
    private TWithdrawOrderMapper tWithdrawOrderMapper;*/
    @Autowired
    private TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
    
    @Autowired
    private TAccountMapper tAccountMapper;
    
    @Autowired
    private TPayLimitMapper tPayLimitMapper;
    @Autowired
    private TAccountPasswordMapper tAccountPasswordMapper;
    
    @Autowired
    private TAccountBalanceMapper tAccountBalanceMapper;

    @Autowired
    private PayBalanceBusiness payBalanceBusiness;

    @Autowired
    private PayRestfulUtil restfulUtil;
    
    @Autowired
    private AppConfigBusiness tAppConfigBusiness;
    
  //  @Autowired
    //private UserLoanStateQueryService userLoanStateQueryService;
    
    @Autowired
    private AccountBusiness accountBusiness;
    
    @Autowired
    private TAccountBalanceSnapshotMapper tAccountBalanceSnapshotMapper;
    
    @Autowired
    private RocketMQUtils rocketMQUtils;
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;
    
    @Autowired
    DynamicConfig dynamicConfig;
    
    @Autowired
    UserInventoryAccountQueryService userInventoryAccountQueryService;
    
    private final static String MQ_TOPIC_PAY_SUCCESS="DISCREPANCY_MONEY_RESPONSE"; 
    

    
    private TAccount queryAccountByUserId(String userId){
        TAccountExample tAccountExample = new TAccountExample();
        /**
         * 未被删除并且签约过的用户才可充值
         */
        tAccountExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andIsDelEqualTo(PayCenterConstant.ACCOUNT_IS_DELETE_NO);
                //.andIsSignEqualTo(PayCenterConstant.ACCOUNT_IS_SIGN_YES);
        List<TAccount> tAccounts = tAccountMapper
                .selectByExample(tAccountExample);
        if (tAccounts == null || tAccounts.size() <= 0) {
             return null;
        }
        TAccount tAccount = tAccounts.get(0);
        
        return tAccount;
     }
    
    
     private boolean isPasswordValid(Long accountId, String inputPassword) {
         boolean isValid = false;

         TAccountPasswordExample tAccountPasswordExample = new TAccountPasswordExample();
         tAccountPasswordExample.createCriteria().andAccountIdEqualTo(accountId);
         List<TAccountPassword> tAccountPasswords =
                 tAccountPasswordMapper.selectByExample(tAccountPasswordExample);
         if (tAccountPasswords == null || tAccountPasswords.size() <= 0) {
             logger.debug(" isPasswordValid password have not set  ");
             return isValid;
         }
         TAccountPassword tAccountPassword = tAccountPasswords.get(0);
         // String password = rechargeRequest.getPassword();
         if (!MD5.sign(inputPassword, tAccountPassword.getSalt(), "UTF-8").equals(
                 tAccountPassword.getPassword())) {
             logger.debug(" isPasswordValid password wrong  ");
             return isValid;
         }
         isValid = true;
         return isValid;
     }
    
     
     private boolean isCanWithdraw(Long accountId) {
         boolean isCanRecharge=true;
         /**
          * 出入金限制
          */
         TPayLimitExample tPayLimitExample = new TPayLimitExample();
         tPayLimitExample.createCriteria().andAccountIdEqualTo(accountId);
         List<TPayLimit> tPayLimits = tPayLimitMapper
                 .selectByExample(tPayLimitExample);
         if (tPayLimits != null && tPayLimits.size() > 0) {
             TPayLimit tPayLimit = tPayLimits.get(0);
             if (PayCenterConstant.ACCOUNT_IS_CAN_NO_RECHARGE == tPayLimit
                     .getCanNotRecharge()) {
                 isCanRecharge=false;
                 return isCanRecharge;
             }
         }
         return isCanRecharge;
     }
     
     @Transactional(propagation=Propagation.REQUIRED)
     TRechargeWithdrawOrder insertWithdraw(Long accountId,Long amount,String userId,String orgId,Date now,String signAccountId,String signChannel){
         TRechargeWithdrawOrder tWithdrawOrder = new TRechargeWithdrawOrder();
         final String orderId=IDGenerator.next().toString();
         tWithdrawOrder.setAccountId(accountId);
         tWithdrawOrder.setAmount(amount);
         tWithdrawOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_INIT);
         tWithdrawOrder.setOrderId(orderId);
         tWithdrawOrder.setStatus(PayCenterConstant.STATUS_DOING);
         tWithdrawOrder.setUserId(userId);
         tWithdrawOrder.setCreateTime(now);
         tWithdrawOrder.setOrgId(orgId);
         tWithdrawOrder.setUpdateTime(now);
         tWithdrawOrder.setSignAccountId(signAccountId);
         tWithdrawOrder.setSignChannel(signChannel);
         tWithdrawOrder.setOrderType("OUT");
         tWithdrawOrder.setBusinessType("CH");
         this.saveWithdraw(tWithdrawOrder);
         TRechargeWithdrawOrderExample tRechargeWithdrawOrderExample=new TRechargeWithdrawOrderExample();
         tRechargeWithdrawOrderExample.createCriteria().andOrderIdEqualTo(orderId);
         List<TRechargeWithdrawOrder> tRechargeWithdrawOrders=tRechargeWithdrawOrderMapper.selectByExample(tRechargeWithdrawOrderExample);
         return tRechargeWithdrawOrders.get(0);
     }
    
    
    
    public WithdrawResponse withdrawRequest(TransferRequest withdrawRequest) throws PayException, PasswordException {
        BaseResponse<WithdrawResponse> result = new BaseResponse<WithdrawResponse>();
        this.checkWithdrawRequestParamp(withdrawRequest);
        WithdrawResponse withdrawResponse = new WithdrawResponse();
        String userId=withdrawRequest.getUserId();
        TAccount tAccount = queryAccountByUserId(userId);

        if(tAccount==null){
            throw new PayException("请先注册!");
        }
        
        Long accountId = tAccount.getAccountId();
        String inputPassword = withdrawRequest.getPassword();
        Long amount = withdrawRequest.getAmount();
        String orgId = withdrawRequest.getOrgId();

        boolean isPwdValid = isPasswordValid(accountId, inputPassword);
        if (!isPwdValid) {
            throw new PasswordException("密码未设置或者密码错误 ! !");
        }
        if(amount<=0){
            throw new PayException("非法金额!");
        }
        boolean isCanRecharge = isCanWithdraw(accountId);
        if (!isCanRecharge) {
            throw new PayException("当前不能出金 !");
        }

        BaseResponse<Long> aresult = callWithdrawAvaliableInterface(accountId);
        if (BaseResponse.RC_FAIL == aresult.getRc()) {
            throw new PayException("当前不能出金 !");
        }

        Long avaliable=aresult.getData();
        if(avaliable==null||avaliable<=0||avaliable.longValue()<amount.longValue()){
            throw new PayException("当前没有可出金额 !");
        }

        
        /**
         * TODO 出入金规则 限制入金金额
         * */
        Long withdrawAmount=this.queryCanWithdrawAmount(userId);
        if(withdrawAmount<=0){
            throw new PayException("当前没有可出金额  !"); 
        }
        
        Date now = new Date();
        TRechargeWithdrawOrder tWithdrawOrder =null;
        try {
             tWithdrawOrder =
                    saveOrderAndSubAvalaible(accountId, amount, userId, orgId, now,
                            tAccount.getSignAccountId(), tAccount.getSignChannel());
        } catch (BalanceNotEnoughException e) {
            throw new PayException("资金不足!");
        }
  
        withdrawResponse.setOrderId(tWithdrawOrder.getOrderId());
        withdrawResponse.setAmount(amount);
        withdrawResponse.setCreateTime(now);
        withdrawResponse.setOrderStatus(PayCenterConstant.STATUS_DOING);
        
        tWithdrawOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_REQUEST);
        int count = this.UpdateRechargeStatus(tWithdrawOrder);
        if (count > 0) {
            /**
             * 调用清结算出入金服务
             */
            BaseResponse<WithdrawResponse> remoteResult =
                    this.remoteWithdrawRequset(tWithdrawOrder.getUserId(),
                            tWithdrawOrder.getAccountId(), tWithdrawOrder.getAmount(),
                            tWithdrawOrder.getOrderId());
            if (remoteResult == null || BaseResponse.RC_SUCCESS != remoteResult.getRc()) {
                throw new PayException("提现失败");
            }
            logger.info("withdrawRequest   remoteResult:{} ",JSON.toJSONString(remoteResult));
            result.setRc(BaseResponse.RC_SUCCESS);
            if (remoteResult.getData()==null) {
                return withdrawResponse;
            }
            withdrawResponse = remoteResult.getData();
            String backStatus = withdrawResponse.getOrderStatus();
           // backStatus=PayCenterConstant.STATUS_DOING;
            
            if (StringUtils.isEmpty(backStatus)) {
                return withdrawResponse;
            }
            if ((!PayCenterConstant.STATUS_SUCCESS.equals(backStatus) && !PayCenterConstant.STATUS_FAIL
                    .equals(backStatus))) {
                return withdrawResponse;
            }
            
            /**
             * 同步返回状态 更新到数据库
             */
            try{
                updateStatusAndWithdrawBalanceAndSendMq(backStatus, tWithdrawOrder.getOrderId(),
                        tWithdrawOrder.getUserId(), tWithdrawOrder.getAmount());
            }catch(BalanceNotEnoughException e){
                throw new PayException("资金不足!"); 
            }
          


        }
        return withdrawResponse;
    }

    public String asyStatusUpdate(WithdrawNotify withdrawNotify) {
        String isOk = "notOk";
        this.checkAsyRequestParamp(withdrawNotify);

        /**
         * TODO 签名校验
         */
        if ("".equals(withdrawNotify.getSign())) {

        }
        TRechargeWithdrawOrderExample tRechargeOrderExample = new TRechargeWithdrawOrderExample();
        tRechargeOrderExample.createCriteria().andOrderIdEqualTo(withdrawNotify.getOrderId()).andOrderTypeEqualTo("OUT");
        List<TRechargeWithdrawOrder> tWithdrawOrders =
                tRechargeWithdrawOrderMapper.selectByExample(tRechargeOrderExample);
        if (tWithdrawOrders == null || tWithdrawOrders.size() <= 0) {
            return isOk;
        }
        TRechargeWithdrawOrder tWithdrawOrder = tWithdrawOrders.get(0);
        if (PayCenterConstant.STATUS_SUCCESS.equals(tWithdrawOrder.getStatus())
                || PayCenterConstant.STATUS_FAIL.equals(tWithdrawOrder.getStatus())) {
            isOk = "OK";
            return isOk;
        }
        String backStatus = withdrawNotify.getOrderStatus();
        if (PayCenterConstant.STATUS_SUCCESS.equals(backStatus)
                || PayCenterConstant.STATUS_FAIL.equals(backStatus)) {

            updateStatusAndWithdrawBalanceAndSendMq(backStatus, tWithdrawOrder.getOrderId(),
                    tWithdrawOrder.getUserId(), tWithdrawOrder.getAmount());
            isOk = "OK";
        }
        return isOk;

    }
    
    public void sendWithdrawSuccessMq(String rechargeStatus,String userId,Long amount){
        try {
            
            if (!PayCenterConstant.STATUS_SUCCESS.equals(rechargeStatus)) {
            return;
        }
        TAccountBalanceExample tAccountBalanceExample=new TAccountBalanceExample();
        tAccountBalanceExample.createCriteria().andUserIdEqualTo(userId).andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_USEABLE);
        List<TAccountBalance> tAccountBalances= tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
        if(tAccountBalances==null||tAccountBalances.size()<=0){
            return ;
        }
        TAccountBalance tAccountBalance=tAccountBalances.get(0);
        PaySuccessNotify paySuccessNotify = new PaySuccessNotify();
        Long balance= tAccountBalance.getAmount();
        paySuccessNotify.setChangeMoney(-amount);
        paySuccessNotify.setTotalMoney(balance<0?0:balance);
        paySuccessNotify.setType("OUT");
        paySuccessNotify.setUserID(userId);

        rocketMQUtils.send(MQ_TOPIC_PAY_SUCCESS, JSON.toJSONString(paySuccessNotify));
        } catch (Exception e) {
            logger.error(" sendWithdrawSuccessMq mqexception:{}", e);
        }
    }
    
    
    public boolean updateStatusAndWithdrawBalanceAndSendMq(String finalStatus, String orderId,
            String userId, Long amount) {
        boolean isSuccess = this.updateStatusAndWithdrawBalance(finalStatus, orderId);
        if (isSuccess) {
            this.sendWithdrawSuccessMq(finalStatus, userId, amount);
        }

        return isSuccess;
    }
    
    
    public boolean updateStatusAndWithdrawBalance(String finalStatus, String orderId) {
        boolean isSuccess = false;

        if ((!PayCenterConstant.STATUS_SUCCESS.equals(finalStatus) && !PayCenterConstant.STATUS_FAIL
                .equals(finalStatus))) {
            return isSuccess;
        }
        TRechargeWithdrawOrderExample tWithdrawOrderExample = new TRechargeWithdrawOrderExample();
        tWithdrawOrderExample.createCriteria().andOrderIdEqualTo(orderId).andOrderTypeEqualTo("OUT");
        List<TRechargeWithdrawOrder> tWithdrawOrders =
                tRechargeWithdrawOrderMapper.selectByExample(tWithdrawOrderExample);
        if (tWithdrawOrders == null || tWithdrawOrders.size() <= 0) {
            return false;
        }
        TRechargeWithdrawOrder tWithdrawOrder = tWithdrawOrders.get(0);
        /**
         * 同步返回状态 更新到数据库
         */
        tWithdrawOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_RESPONSE);
        int respCount = this.UpdateRechargeStatus(tWithdrawOrder);

        if (respCount <= 0) {
            return false;
        }

        tWithdrawOrder.setStatus(finalStatus);
        this.UpdateRechargeStatus(tWithdrawOrder);
        if (PayCenterConstant.STATUS_FAIL.equals(finalStatus)) {
            /**
             * 资金和流水
             */
            WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
            withdrawBalanceRequest.setAmount(tWithdrawOrder.getAmount());
            withdrawBalanceRequest.setEntrustNum(tWithdrawOrder.getOrderId());
            withdrawBalanceRequest.setOrgId(tWithdrawOrder.getOrgId());
            withdrawBalanceRequest.setUserId(tWithdrawOrder.getUserId());
            payBalanceBusiness.addAvalaibleBalanceInWthdraw(withdrawBalanceRequest);;
        }
        if(PayCenterConstant.STATUS_SUCCESS.equals(finalStatus)){
            isSuccess = true;
        }
        return isSuccess;
    }


    public BaseResponse<WithdrawResponse> remoteWithdrawRequset(String userId, Long accountId,
            Long amount, String orderId) {
        return restfulUtil.withdraw(userId, accountId, amount, orderId);
    }

    public void checkAsyRequestParamp(WithdrawNotify rechargeNotify) {
        if (rechargeNotify.getAmount() == null || rechargeNotify.getAmount() < 1) {
            throw new IllegalArgumentException("parameter Amount can not be null");
        }
        if (rechargeNotify.getOrderId() == null) {
            throw new IllegalArgumentException("parameter OrderId can not be null");
        }
        if (rechargeNotify.getOrderStatus() == null) {
            throw new IllegalArgumentException("parameter OrderStatus can not be null");
        }
        if (rechargeNotify.getSign() == null) {
            throw new IllegalArgumentException("parameter Sign can not be null");
        }
    }

    
    /**
     * 查詢可提現金額
     * 
     * @param userId
     * @return
     */
    public Map<Long,Long> queryUsersCanWithdrawAmount(List<String> userIds) {
        
        TAccountExample tAccountExample = new TAccountExample();
        /**
         * 未被删除并且签约过的用户才可出金
         */
        tAccountExample.createCriteria().andUserIdIn(userIds)
                .andIsDelEqualTo(PayCenterConstant.ACCOUNT_IS_DELETE_NO);
              //  .andIsSignEqualTo(PayCenterConstant.ACCOUNT_IS_SIGN_YES);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(tAccountExample);
        
        /**
         * 是否有融资 
         */
        TAccountBalanceExample tAccountLOANBalanceExample = new TAccountBalanceExample();
        tAccountLOANBalanceExample.createCriteria().andUserIdIn(userIds)
                //.andAccountIdEqualTo(tAccount.getAccountId())
                .andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_LOAN);
        List<TAccountBalance> tAccountLoanBalances =
                tAccountBalanceMapper.selectByExample(tAccountLOANBalanceExample);
        Map<Long,Long> loanMap=new HashMap<Long,Long>();
        for (int i = 0; i < tAccountLoanBalances.size(); i++) {
            TAccountBalance tAccountBalance=tAccountLoanBalances.get(i);
            Long loanAmount= tAccountBalance.getAmount();
            loanMap.put(tAccountBalance.getAccountId(), loanAmount);
        }
        
        
        /**
         * 是否有融货
         */
        QueryUserInventoryAcctListByIDsRequest queryUserInventoryAcctListByIDsRequest=new QueryUserInventoryAcctListByIDsRequest();
        List<Long> userIDList=new ArrayList<Long>();
        for (String userId : userIds) {
            userIDList.add(Long.valueOf(userId));
        }
        queryUserInventoryAcctListByIDsRequest.setUserIDList(userIDList);
        List<UserInventoryAccountDTO> userInventoryAccountDTOs= userInventoryAccountQueryService.queryUserInventoryAcctListByUserIDs(queryUserInventoryAcctListByIDsRequest);
        Map<Long,Long> inventoryMap=new HashMap<Long,Long>();
        for (UserInventoryAccountDTO userInventoryAccountDTO : userInventoryAccountDTOs) {
            Long loanAmount= userInventoryAccountDTO.getLoanQuantity();
            inventoryMap.put(userInventoryAccountDTO.getUserId(), loanAmount);
        }
      
        TAccountBalanceExample tAccountBalanceExample = new TAccountBalanceExample();
        tAccountBalanceExample.createCriteria().andUserIdIn(userIds)
                .andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_USEABLE);
        List<TAccountBalance> tAccountBalances =
                tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
        Map<Long,Long> balanceMap=new HashMap<Long,Long>();
        for (TAccountBalance tAccountBalance : tAccountBalances) {
            balanceMap.put(tAccountBalance.getAccountId(), tAccountBalance.getAmount());
        }
       
        Map<Long,Long> withdrawAmountMap=new HashMap<Long,Long>();
        
        for (int i = 0; i < tAccounts.size(); i++) {
            TAccount tAccount = tAccounts.get(i);
            Long accountId = tAccount.getAccountId();
            String userId = tAccount.getUserId();

            Long userLoan = loanMap.get(accountId);
            if (userLoan != null && userLoan > 0) {
                withdrawAmountMap.put(accountId, 0L);
                continue;
            }
            Long loanInventory = inventoryMap.get(Long.valueOf(userId));
            if (loanInventory != null &&loanInventory > 0) {
                withdrawAmountMap.put(accountId, 0L);
                continue;
            }
            
            Long settled= this.queryUserbalanceSnapShot(userId);
            Long userable=balanceMap.get(accountId)==null?0L:balanceMap.get(accountId);
            withdrawAmountMap.put(accountId, Math.min(userable, settled==null?0L:settled));
        }
        return withdrawAmountMap;
    }

    
    
    
    
    /**
     * 查詢可提現金額
     * 
     * @param userId
     * @return
     */
    public Long queryCanWithdrawAmount(String userId) {
        TAccountExample tAccountExample = new TAccountExample();
        /**
         * 未被删除并且签约过的用户才可充值
         */
        tAccountExample.createCriteria().andUserIdEqualTo(userId)
                .andIsDelEqualTo(PayCenterConstant.ACCOUNT_IS_DELETE_NO);
              //  .andIsSignEqualTo(PayCenterConstant.ACCOUNT_IS_SIGN_YES);
        List<TAccount> tAccounts = tAccountMapper.selectByExample(tAccountExample);
        if (tAccounts == null || tAccounts.size() <= 0) {
            return 0L;
        }
        TAccount tAccount = tAccounts.get(0);
        /**
         * 是否有融资 
         */
        TAccountBalanceExample tAccountLOANBalanceExample = new TAccountBalanceExample();
        tAccountLOANBalanceExample.createCriteria().andUserIdEqualTo(userId)
                .andAccountIdEqualTo(tAccount.getAccountId())
                .andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_LOAN);
        List<TAccountBalance> tAccountLoanBalances =
                tAccountBalanceMapper.selectByExample(tAccountLOANBalanceExample);
        long loanAmount = 0L;
        if (tAccountLoanBalances != null && tAccountLoanBalances.size() > 0) {
            loanAmount = tAccountLoanBalances.get(0).getAmount();
        }
        if(loanAmount>0){
            return 0L;
        }
        
        /**
         * 是否有融货
         */
        QueryUserInventoryAcctListByIDsRequest queryUserInventoryAcctListByIDsRequest=new QueryUserInventoryAcctListByIDsRequest();
        List<Long> userIDList=new ArrayList<Long>();
        userIDList.add(Long.valueOf(userId));
        queryUserInventoryAcctListByIDsRequest.setUserIDList(userIDList);
        List<UserInventoryAccountDTO> userInventoryAccountDTOs= userInventoryAccountQueryService.queryUserInventoryAcctListByUserIDs(queryUserInventoryAcctListByIDsRequest);
        for (UserInventoryAccountDTO userInventoryAccountDTO : userInventoryAccountDTOs) {
            if(userInventoryAccountDTO.getLoanQuantity()>0){
                return 0L;
            }
        }
      
        TAccountBalanceExample tAccountBalanceExample = new TAccountBalanceExample();
        tAccountBalanceExample.createCriteria().andUserIdEqualTo(userId)
                .andAccountIdEqualTo(tAccount.getAccountId())
                .andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_USEABLE);
        List<TAccountBalance> tAccountBalances =
                tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
        long userable = 0L;
        if (tAccountBalances != null && tAccountBalances.size() > 0) {
            userable = tAccountBalances.get(0).getAmount();
        }
        Date now=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        
        Calendar endcalendar=Calendar.getInstance();
        endcalendar.setTime(now);
        endcalendar.set(Calendar.HOUR_OF_DAY, 23);
        endcalendar.set(Calendar.MINUTE, 59);
        endcalendar.set(Calendar.SECOND, 59);
        endcalendar.set(Calendar.MILLISECOND,59);
        
        TRechargeWithdrawOrderExample tWithdrawOrderExample=new TRechargeWithdrawOrderExample();
        tWithdrawOrderExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId()).andCreateTimeGreaterThan(calendar.getTime()).andCreateTimeLessThan(endcalendar.getTime()).andOrderTypeEqualTo("OUT");
        List<TRechargeWithdrawOrder> tWithdrawOrders=tRechargeWithdrawOrderMapper.selectByExample(tWithdrawOrderExample);
        long total=0L;
        for (TRechargeWithdrawOrder tWithdrawOrder : tWithdrawOrders) {
            total=total+tWithdrawOrder.getAmount();
        }
        
        Long settled= this.queryUserbalanceSnapShot(userId);
        Long canWithdraw=Math.min(userable, settled==null?0L:settled);
        if(canWithdraw!=null){
            canWithdraw=canWithdraw-total;
        }
        return canWithdraw<0?0:canWithdraw;
    }

    @Transactional
    public void saveWithdraw(TRechargeWithdrawOrder record) {
        tRechargeWithdrawOrderMapper.insert(record);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public int UpdateRechargeStatus(TRechargeWithdrawOrder record) {
        return tRechargeWithdrawOrderMapper.updateByPrimaryKeySelective(record);
    }

    public void checkWithdrawRequestParamp(TransferRequest withdrawRequest) {
        if (withdrawRequest.getAmount() == null || withdrawRequest.getAmount() < 1) {
            throw new IllegalArgumentException("parameter amount can not be null");
        }
        if (withdrawRequest.getPassword() == null) {
            throw new IllegalArgumentException("parameter Password can not be null");
        }
        if (withdrawRequest.getSerialNo() == null) {
            throw new IllegalArgumentException("parameter SerialNo can not be null");
        }
        if (withdrawRequest.getUserId() == null) {
            throw new IllegalArgumentException("parameter UserId can not be null");
        }
        /** if (StringUtils.isBlank(withdrawRequest.getOrgId())) {
            throw new IllegalArgumentException("parameter OrgId can not be null");
        }**/
    }


    public TransferRecord toWithdrawResponse(TRechargeWithdrawOrder tWithdrawOrder) {
        TransferRecord result = new TransferRecord();
        result.setOrderId(tWithdrawOrder.getOrderId());
        result.setAmount(tWithdrawOrder.getAmount());
        result.setStatus(tWithdrawOrder.getStatus());
        result.setCreateTime(tWithdrawOrder.getCreateTime());
        result.setType("OUT");
        result.setUserId(tWithdrawOrder.getUserId());
        result.setSignedAccount(tWithdrawOrder.getSignAccountId());
        result.setSignedBank(tWithdrawOrder.getSignChannel());
        return result;
    }

    public PageResponse<List<TransferRecord>> queryWithdrawLogByUserId(
            QueryTransferRecordsRequest wthdrawSearchRequest) {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.setOrderByClause(" create_time DESC");
        TRechargeWithdrawOrderExample.Criteria criteria = example.createCriteria();
        if(wthdrawSearchRequest.getEndTime()!=null){
            criteria.andCreateTimeLessThanOrEqualTo(wthdrawSearchRequest.getEndTime());
        }
        if(wthdrawSearchRequest.getStartTime()!=null){
            criteria.andCreateTimeGreaterThanOrEqualTo(wthdrawSearchRequest.getStartTime()); 
        }
        
        if (StringUtils.isNotBlank(wthdrawSearchRequest.getStatus())) {
            criteria.andStatusEqualTo(wthdrawSearchRequest.getStatus());
        }
        if (wthdrawSearchRequest.getUserIds() != null&&wthdrawSearchRequest.getUserIds().size()>0) {
            criteria.andUserIdIn(wthdrawSearchRequest.getUserIds());
        }

        if (wthdrawSearchRequest.getOrgIds() != null&&wthdrawSearchRequest.getOrgIds().size()>0) {
            criteria.andOrgIdIn(wthdrawSearchRequest.getOrgIds());
        }
        if(!StringUtils.isBlank(wthdrawSearchRequest.getOrderId())){
            criteria.andOrderIdEqualTo(wthdrawSearchRequest.getOrderId());
        }
        Page<Object> page =
                PageHelper.startPage(wthdrawSearchRequest.getCurrentPage()<=0?1:wthdrawSearchRequest.getCurrentPage(),
                        wthdrawSearchRequest.getPageSize());
        criteria.andOrderTypeEqualTo("OUT");
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        List<TransferRecord> resList = new ArrayList<TransferRecord>();
        for (TRechargeWithdrawOrder tWithdrawOrder : list) {
            resList.add(this.toWithdrawResponse(tWithdrawOrder));
        }

       /** PageBaseResponse<TransferRecord> pageRspData =
                new PageBaseResponse<TransferRecord>(page.getTotal(),
                        wthdrawSearchRequest.getPageNo(),
                        wthdrawSearchRequest.getPageSize(), resList);**/
        PageResponse<List<TransferRecord>> pageRspData =new PageResponse<List<TransferRecord>>();
        pageRspData.setCurrentPage(wthdrawSearchRequest.getCurrentPage());
        pageRspData.setData(resList);
        pageRspData.setPageSize(wthdrawSearchRequest.getPageSize());
        pageRspData.setTotal(page.getTotal());
        return pageRspData;
    }

    
    
    public WithdrawNotify queryWithdrawStatus(String orderId) {
        WithdrawNotify withdrawNotify = new WithdrawNotify();
        TRechargeWithdrawOrder tWithdrawOrder = qryTWithdrawOrderByOrderId(orderId);
        if (tWithdrawOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_DOING)) {
            TAppConfig tAppConfig = tAppConfigBusiness.qryTAppConfigByAppId("CH");
            if (tAppConfig != null) {
                String notifyUrl = tAppConfig.getWithdrawNotifyUrl() + orderId;
                String appSecret = tAppConfig.getAppSrcret();
                if (StringUtils.isNotBlank(notifyUrl)) {
                    // 对方明确返回OK，则将记录标识为已通知
                    String result =
                            callWithdrawNotifyInterface(tWithdrawOrder, notifyUrl, appSecret);
                    logger.info("call orderId {} url+result: {} ", orderId, notifyUrl + ":"
                            + result);
                    BaseResponse<RechargeResponse> qresult = new BaseResponse<RechargeResponse>();
                    if (result != null && !result.trim().equals("")) {
                        JSONObject responseJson = JSON.parseObject(result);
                        Integer rc =
                                responseJson.getInteger("rc") == null ? -1 : responseJson
                                        .getInteger("rc");
                        qresult.setRc(rc);
                        qresult.setMsg(responseJson.getString("msg"));
                        if (BaseResponse.RC_SUCCESS == rc) {
                            withdrawNotify =
                                    JSON.parseObject(responseJson.getString("data"),
                                            WithdrawNotify.class);
                        }
                    }


                }
            }
        }
        return withdrawNotify;
    }
    
    
    public TRechargeWithdrawOrder qryTWithdrawOrderByOrderId(String orderId) {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    
    
    //调用业务系统提供的入金通知接口
    private String callWithdrawNotifyInterface(TRechargeWithdrawOrder tWineRechargeOrder, String url, String appSecret){
        Map<String, String> headParams = new HashMap<String, String>();
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        WithdrawNotify withdrawNotify = new WithdrawNotify();
        withdrawNotify.setOrderId(tWineRechargeOrder.getOrderId());
        String message = JSON.toJSONString(withdrawNotify);
        String html = "";
        try {
            logger.info("post {} to url {}", message, url);
            html = HttpClientUtils.doPost(url, headParams, message);
        } catch (Exception e) {
           throw new IllegalArgumentException(e.getMessage());
        }
        return html;
    }
    


    /**
     * 最新已結算金額
     * 
     * @param userId
     * @return
     */
    public Long queryUserbalanceSnapShot(String userId) {
        Long settledAmount = null;
        TAccountBalanceSnapshotExample example = new TAccountBalanceSnapshotExample();
        example.createCriteria().andUserIdEqualTo(userId)
                .andBalanceTypeEqualTo(PayCenterConstant.ACCOUNT_BALANCE_TYPE_USEABLE);
        example.setOrderByClause("id desc");
        List<TAccountBalanceSnapshot> tAccountBalanceSnapshots =
                tAccountBalanceSnapshotMapper.selectByExample(example);
        if (tAccountBalanceSnapshots != null && tAccountBalanceSnapshots.size() > 0) {
            settledAmount = tAccountBalanceSnapshots.get(0).getAmount();
        }

        return settledAmount;
    }

    
    
    private BaseResponse<Long> callWithdrawAvaliableInterface(Long accountId) {
        BaseResponse<Long> result = new BaseResponse<Long>();
        String url = dailyTaskConfig.getSettlementCustomerBaseUrl()+"/balance/avaliable/" + accountId;
        String json =
                this.callWithdrawAvaliableInterface(JSON.parse(accountId==null?"":accountId.toString()).toString(), url, "");
        JSONObject responseJson = JSON.parseObject(json);
        Integer rc = responseJson.getInteger("rc") == null ? -1 : responseJson.getInteger("rc");
        result.setRc(rc);
        if (BaseResponse.RC_SUCCESS == rc) {
            Long avaliable = responseJson.getLong("data");
            result.setData(avaliable);
        }
        return result;
    }
    
    
    private String callWithdrawAvaliableInterface(String accountId, String url, String appSecret){
        Map<String, String> params=new HashMap<String, String>();
        params.put("accountId", accountId);
        return this.callWithdrawAvaliableInterface(params, url, appSecret);
    }
    
    //调用业务系统提供的入金通知接口
    private String callWithdrawAvaliableInterface(  Map<String, String> params,String url, String appSecret){
        Map<String, String> headParams = new HashMap<String, String>();
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        String html = "";
        try {
            logger.info("post {} to url {}", JSON.toJSONString(params), url);
           
            html = HttpClientUtils.doGet(url, headParams, params);
        } catch (Exception e) {
            logger.error(" callInterface url:{} exception:{}",url,e);
           throw new IllegalArgumentException(e.getMessage());
        }
        return html;
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public void subAvailable(TRechargeWithdrawOrder tWithdrawOrder){
        /**
         * 资金和流水
         */
        WithdrawBalanceRequest withdrawBalanceRequest = new WithdrawBalanceRequest();
        withdrawBalanceRequest.setAmount(tWithdrawOrder.getAmount());
        withdrawBalanceRequest.setEntrustNum(tWithdrawOrder.getOrderId());
        withdrawBalanceRequest.setOrgId(tWithdrawOrder.getOrgId());
        withdrawBalanceRequest.setUserId(tWithdrawOrder.getUserId());
        payBalanceBusiness.subAvalaibleInWthdraw(withdrawBalanceRequest);
    }
    
    @Transactional(propagation=Propagation.REQUIRED)
    public TRechargeWithdrawOrder saveOrderAndSubAvalaible( Long accountId,Long amount,String userId,String orgId,Date now,String signAccountId,String signChannel){
        TRechargeWithdrawOrder tWithdrawOrder = this.insertWithdraw(accountId, amount, userId, orgId, now, signAccountId, signChannel);
        this.subAvailable(tWithdrawOrder);
        return tWithdrawOrder;
        
    }
}
