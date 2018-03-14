package com.baibei.accountservice.paycenter.bussiness.ch;

import java.util.ArrayList;
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
import com.baibei.account.dto.notify.PaySuccessNotify;
import com.baibei.account.dto.request.QueryTransferRecordsRequest;
import com.baibei.account.dto.request.TransferRequest;
import com.baibei.account.dto.response.PageResponse;
import com.baibei.account.dto.response.TransferRecord;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TAccountPasswordMapper;
import com.baibei.accountservice.dao.TPayLimitMapper;
import com.baibei.accountservice.dao.TRechargeWithdrawOrderMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TAccountPassword;
import com.baibei.accountservice.model.TAccountPasswordExample;
import com.baibei.accountservice.model.TAppConfig;
import com.baibei.accountservice.model.TPayLimit;
import com.baibei.accountservice.model.TPayLimitExample;
import com.baibei.accountservice.model.TRechargeWithdrawOrder;
import com.baibei.accountservice.model.TRechargeWithdrawOrderExample;
import com.baibei.accountservice.paycenter.bussiness.AppConfigBusiness;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.dto.base.BaseResponse;
import com.baibei.accountservice.paycenter.dto.request.RechargeBalanceRequest;
import com.baibei.accountservice.paycenter.dto.response.RechargeResponse;
import com.baibei.accountservice.paycenter.exception.PasswordException;
import com.baibei.accountservice.paycenter.exception.PayException;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.utill.PayRestfulUtil;
import com.baibei.accountservice.paycenter.vo.response.RechargeNotify;
import com.baibei.accountservice.rocketmq.RocketMQUtils;
import com.baibei.accountservice.util.IDGenerator;
import com.baibei.accountservice.util.MD5;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class RechargeBussiness {
    private static final Logger logger = LoggerFactory.getLogger(RechargeBussiness.class);
//	@Autowired
//	private TRechargeOrderMapper tRechargeOrderMapper;
    @Autowired
    private TRechargeWithdrawOrderMapper tRechargeWithdrawOrderMapper;
	@Autowired
	private TAccountMapper tAccountMapper;
	@Autowired
	private TPayLimitMapper tPayLimitMapper;
	@Autowired
	private TAccountPasswordMapper tAccountPasswordMapper;
	@Autowired
	private PayRestfulUtil restfulUtil;
	@Autowired
	private PayBalanceBusiness payBalanceBusiness;
    
    @Autowired
    private AppConfigBusiness tAppConfigBusiness;
    
    @Autowired
    private RocketMQUtils rocketMQUtils;
    
    @Autowired
    private TAccountBalanceMapper tAccountBalanceMapper;
 
    
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
   
    
    private boolean isCanRecharge(Long accountId) {
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
    TRechargeWithdrawOrder insertRecharge(Long accountId,Long amount,String userId,String orgId,Date now,String signAccountId,String signChannel){
        TRechargeWithdrawOrder tRechargeOrder = new TRechargeWithdrawOrder();
        final String orderId=IDGenerator.next().toString();
        tRechargeOrder.setAccountId(accountId);
        tRechargeOrder.setAmount(amount);
        tRechargeOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_INIT);
        tRechargeOrder.setOrderId(orderId);
        tRechargeOrder.setStatus(PayCenterConstant.STATUS_DOING);
        tRechargeOrder.setUserId(userId);
        tRechargeOrder.setCreateTime(now);
        tRechargeOrder.setOrgId( StringUtils.isBlank(orgId)?"":orgId);
        tRechargeOrder.setUpdateTime(now);
        tRechargeOrder.setSignAccountId(signAccountId);
        tRechargeOrder.setOrderType("IN");
        tRechargeOrder.setSignChannel(signChannel);
        tRechargeOrder.setBusinessType("CH");
        this.saveRecharge(tRechargeOrder);
        TRechargeWithdrawOrderExample tRechargeWithdrawOrderExample=new TRechargeWithdrawOrderExample();
        tRechargeWithdrawOrderExample.createCriteria().andOrderIdEqualTo(orderId);
        List<TRechargeWithdrawOrder> tRechargeWithdrawOrders=tRechargeWithdrawOrderMapper.selectByExample(tRechargeWithdrawOrderExample);
        return tRechargeWithdrawOrders.get(0);
    }
    
    
    public void sendRechargeSuccessMq(String rechargeStatus,String userId,Long amount){
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
        paySuccessNotify.setChangeMoney(amount);
        Long balance= tAccountBalance.getAmount()-amount;
        paySuccessNotify.setTotalMoney(balance<0?0:balance);
        paySuccessNotify.setType("IN");
        paySuccessNotify.setUserID(userId);
       
            rocketMQUtils.send(MQ_TOPIC_PAY_SUCCESS, JSON.toJSONString(paySuccessNotify));
        } catch (Exception e) {
            logger.error(" recharge mqexception:{}", e);
        }
    }
    
    
	public RechargeResponse rechargeRequest(TransferRequest rechargeRequest) throws PayException, PasswordException {
	    RechargeResponse rechargeResponse=new RechargeResponse();
		this.checkRechargeRequestParamp(rechargeRequest);
		
		String userId=rechargeRequest.getUserId();
		
		TAccount tAccount=this.queryAccountByUserId(userId);
		
		if(tAccount==null){
		    throw new PayException("请先注册!");
		}
	    
        Long accountId = tAccount.getAccountId();
        String inputPassword = rechargeRequest.getPassword();
        Long amount = rechargeRequest.getAmount();
        String orgId = rechargeRequest.getOrgId();


        boolean isPwdValid = isPasswordValid(accountId, inputPassword);
        if (!isPwdValid) {
            throw new PasswordException("密码未设置或者密码错误 ! !");
        }

		boolean isCanRecharge=isCanRecharge(accountId);
	      if(!isCanRecharge){
	            throw new PayException("当前不能充值 !");
	        }
		if(amount<=0){
		    throw new PayException("非法金额!");
		}
		/**
		 * TODO 出入金规则 限制入金金额
		 */
		Date now = new Date();
		/**
		 * 订单持久化拥有单独的事物，防止调用清结算失败导致回滚（例如 ，充值订单在清结算成功，返回超时异常，导致事物回滚）
		 */
		TRechargeWithdrawOrder tRechargeOrder= insertRecharge(accountId, amount, userId, orgId, now,tAccount.getSignAccountId(),tAccount.getSignChannel());
		
		tRechargeOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_REQUEST);
		
		int count = this.UpdateRechargeStatus(tRechargeOrder);
		if (count > 0) {
			/**
			 * 调用清结算出入金服务
			 */
			BaseResponse<RechargeResponse> remoteResult = this.remoteRechargeRequset(tRechargeOrder);
			if(remoteResult==null||BaseResponse.RC_SUCCESS!=remoteResult.getRc()){
			    throw new PayException(remoteResult.getMsg());
			}
			
            rechargeResponse = remoteResult.getData();
            String backStatus = rechargeResponse.getOrderStatus();
            if (StringUtils.isEmpty(backStatus)) {
                return rechargeResponse;
            }
            if ((!PayCenterConstant.STATUS_SUCCESS.equals(backStatus)&&!PayCenterConstant.STATUS_FAIL
                    .equals(backStatus))) {
                return rechargeResponse;
            }
            /**
             * 同步返回状态 更新到数据库
             */
            //updateStatusAndRechargeBalance(backStatus, tRechargeOrder.getOrderId());
            updateStatusAndRechargeBalanceAndSendMq(backStatus, tRechargeOrder.getOrderId(),
                    tRechargeOrder.getUserId(), tRechargeOrder.getAmount());
		}
		return rechargeResponse;
	}
	

    public boolean updateStatusAndRechargeBalanceAndSendMq(String finalStatus, String orderId,
            String userId, Long amount) {
        boolean isSuccess = this.updateStatusAndRechargeBalance(finalStatus, orderId);
        if (isSuccess) {
            this.sendRechargeSuccessMq(finalStatus, userId, amount);
        }

        return isSuccess;
    }
	
	
    public boolean updateStatusAndRechargeBalance(String finalStatus, String orderId) {
        boolean isSuccess = false;
        
        if ((!PayCenterConstant.STATUS_SUCCESS.equals(finalStatus)&&!PayCenterConstant.STATUS_FAIL
                .equals(finalStatus))) {
            return isSuccess;
        }
        TRechargeWithdrawOrderExample tRechargeOrderExample = new TRechargeWithdrawOrderExample();
        tRechargeOrderExample.createCriteria().andOrderIdEqualTo(orderId).andOrderTypeEqualTo("IN");
       /* List<TRechargeOrder> tRechargeOrders =
                tRechargeOrderMapper.selectByExample(tRechargeOrderExample);*/
        List<TRechargeWithdrawOrder> tRechargeOrders =   tRechargeWithdrawOrderMapper.selectByExample(tRechargeOrderExample);
        if (tRechargeOrders == null || tRechargeOrders.size() <= 0) {
            return false;
        }
        TRechargeWithdrawOrder tRechargeOrder = tRechargeOrders.get(0);
        /**
         * 同步返回状态 更新到数据库
         */
        tRechargeOrder.setHandleStatus(PayCenterConstant.HANDLE_STATUS_RESPONSE);
        int respCount = this.UpdateRechargeStatus(tRechargeOrder);

        if (respCount <= 0) {
            return false;
        }

        tRechargeOrder.setStatus(finalStatus);
        this.UpdateRechargeStatus(tRechargeOrder);
        if (PayCenterConstant.STATUS_SUCCESS.equals(finalStatus)) {
            /**
             * 资金和流水
             */
            RechargeBalanceRequest rechargeBalanceRequest = new RechargeBalanceRequest();
            rechargeBalanceRequest.setAmount(tRechargeOrder.getAmount());
            rechargeBalanceRequest.setEntrustNum(tRechargeOrder.getOrderId());
            rechargeBalanceRequest.setOrgId(tRechargeOrder.getOrgId()); //
            rechargeBalanceRequest.setUserId(tRechargeOrder.getUserId());
            payBalanceBusiness.rechargeBalance(rechargeBalanceRequest);
            isSuccess = true;
            return isSuccess;
        }
        return isSuccess;
    }
	

	public String asyStatusUpdate(RechargeNotify rechargeNotify) {
		String isOk = "notOk";
		this.checkAsyRequestParamp(rechargeNotify);
		/**
		 * TODO 签名校验
		 */
		if ("".equals(rechargeNotify.getSign())) {

		}
		TRechargeWithdrawOrderExample tRechargeOrderExample = new TRechargeWithdrawOrderExample();
		tRechargeOrderExample.createCriteria().andOrderIdEqualTo(
				rechargeNotify.getOrderId()).andOrderTypeEqualTo("IN");
		List<TRechargeWithdrawOrder> tRechargeOrders = tRechargeWithdrawOrderMapper
				.selectByExample(tRechargeOrderExample);
		if (tRechargeOrders == null || tRechargeOrders.size() <= 0) {
			return isOk;
		}
		TRechargeWithdrawOrder tRechargeOrder = tRechargeOrders.get(0);
		if(PayCenterConstant.STATUS_SUCCESS.equals(tRechargeOrder.getStatus())||PayCenterConstant.STATUS_FAIL.equals(tRechargeOrder.getStatus())){
		    isOk = "OK";
		    return isOk;
		}
		
		
        String finalStatus = rechargeNotify.getOrderStatus();
        // status=PayCenterConstant.STATUS_DOING;
        if (PayCenterConstant.STATUS_SUCCESS.equals(finalStatus)
                || PayCenterConstant.STATUS_FAIL.equals(finalStatus)) {
            // updateStatusAndRechargeBalance(finalStatus, tRechargeOrder.getOrderId());
            updateStatusAndRechargeBalanceAndSendMq(finalStatus, tRechargeOrder.getOrderId(),
                    tRechargeOrder.getUserId(), tRechargeOrder.getAmount());
            isOk = "OK";
        }

		return isOk;

	}

    public BaseResponse<RechargeResponse> remoteRechargeRequset(TRechargeWithdrawOrder tRechargeOrder) {
        return restfulUtil.recharge(tRechargeOrder);
    }

    
    public RechargeNotify queryRechargeStatus(String orderId) {
        RechargeNotify rechargeNotify = new RechargeNotify();
        TRechargeWithdrawOrder tRechargeOrder = qryTRechargeOrderByOrderId(orderId);
        if (tRechargeOrder.getStatus().equalsIgnoreCase(PayCenterConstant.STATUS_DOING)) {
            TAppConfig tAppConfig = tAppConfigBusiness.qryTAppConfigByAppId("CH");
            if (tAppConfig != null) {
                String notifyUrl = tAppConfig.getRechargeNotifyUrl() + orderId;
                String appSecret = tAppConfig.getAppSrcret();
                if (StringUtils.isNotBlank(notifyUrl)) {
                    // 对方明确返回OK，则将记录标识为已通知
                    String result =
                            callRechargeNotifyInterface(tRechargeOrder, notifyUrl, appSecret);
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
                            rechargeNotify =
                                    JSON.parseObject(responseJson.getString("data"),
                                            RechargeNotify.class);
                        }
                    }


                }
            }
        }
        return rechargeNotify;
    }
    
    
    public TRechargeWithdrawOrder qryTRechargeOrderByOrderId(String orderId) {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
    
    
    //调用业务系统提供的入金通知接口
    private String callRechargeNotifyInterface(TRechargeWithdrawOrder tWineRechargeOrder, String url, String appSecret){
        Map<String, String> headParams = new HashMap<String, String>();
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        RechargeNotify rechargeNotify = new RechargeNotify();
        rechargeNotify.setOrderId(tWineRechargeOrder.getOrderId());
        String message = JSON.toJSONString(rechargeNotify);
        String html = "";
        try {
            logger.info("post {} to url {}", message, url);
            html = HttpClientUtils.doPost(url, headParams, message);
        } catch (Exception e) {
            logger.info(" callRechargeNotifyInterface Exception:{} ",e);
           throw new IllegalArgumentException(e.getMessage());
        }
        return html;
    }
    
    
    
   
    
    
	public void checkAsyRequestParamp(RechargeNotify rechargeNotify) {
		if (rechargeNotify.getAmount() == null
				|| rechargeNotify.getAmount() < 1) {
			throw new IllegalArgumentException(
					"parameter Amount can not be null");
		}
		if (rechargeNotify.getOrderId() == null) {
			throw new IllegalArgumentException(
					"parameter OrderId can not be null");
		}
		if (rechargeNotify.getOrderStatus() == null) {
			throw new IllegalArgumentException(
					"parameter OrderStatus can not be null");
		}
	//	if (rechargeNotify.getSign() == null) {
		//	throw new IllegalArgumentException("parameter Sign can not be null");
	//	}
	}

	@Transactional
	public void saveRecharge(TRechargeWithdrawOrder record) {
	    tRechargeWithdrawOrderMapper.insert(record);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public int UpdateRechargeStatus(TRechargeWithdrawOrder tRechargeOrder) {
		return tRechargeWithdrawOrderMapper.updateByPrimaryKeySelective(tRechargeOrder);
	}
	
	
	
    public PageResponse<List<TransferRecord>> queryRechargeLogByUserId(
            QueryTransferRecordsRequest rechargeSearchRequest) {
        TRechargeWithdrawOrderExample example = new TRechargeWithdrawOrderExample();
        example.setOrderByClause(" create_time DESC");
        TRechargeWithdrawOrderExample.Criteria criteria = example.createCriteria();
        if(rechargeSearchRequest.getEndTime()!=null){
            criteria.andCreateTimeLessThanOrEqualTo(rechargeSearchRequest.getEndTime());
        }
        if(rechargeSearchRequest.getStartTime()!=null){
            criteria.andCreateTimeGreaterThanOrEqualTo(rechargeSearchRequest.getStartTime()); 
        }
        
        if (StringUtils.isNotBlank(rechargeSearchRequest.getStatus())) {
            criteria.andStatusEqualTo(rechargeSearchRequest.getStatus());
        }
        if (rechargeSearchRequest.getUserIds() != null&&rechargeSearchRequest.getUserIds().size()>0) {
            criteria.andUserIdIn(rechargeSearchRequest.getUserIds());
        }
        if (rechargeSearchRequest.getOrgIds() != null&&rechargeSearchRequest.getOrgIds().size()>0) {
            criteria.andOrgIdIn(rechargeSearchRequest.getOrgIds());
        }
        if(!StringUtils.isBlank(rechargeSearchRequest.getOrderId())){
            criteria.andOrderIdEqualTo(rechargeSearchRequest.getOrderId());
        }

        log.info(" queryRechargeLogByUserId :{}",JSON.toJSONString(rechargeSearchRequest));
        Page<Object> page =
                PageHelper.startPage(rechargeSearchRequest.getCurrentPage()<=0?1:rechargeSearchRequest.getCurrentPage(),
                        rechargeSearchRequest.getPageSize());
        List<TRechargeWithdrawOrder> list = tRechargeWithdrawOrderMapper.selectByExample(example);
        log.info(" queryRechargeLogByUserId list:{}",JSON.toJSONString(list));
        List<TransferRecord> resList = new ArrayList<TransferRecord>();
        for (TRechargeWithdrawOrder tRechargeOrder : list) {
            resList.add(this.toRechargeResponse(tRechargeOrder));
        }

      /**  PageResponse<List<TransferRecord>> pageRspData =
                new PageResponse<List<TransferRecord>>(page.getTotal(),
                        rechargeSearchRequest.getPageNo(),
                        rechargeSearchRequest.getPageSize(), resList);**/
        log.info(" queryRechargeLogByUserId resList:{}",JSON.toJSONString(resList));
        PageResponse<List<TransferRecord>> pageRspData =new PageResponse<List<TransferRecord>>();
        pageRspData.setCurrentPage(rechargeSearchRequest.getCurrentPage());
        pageRspData.setData(resList);
        pageRspData.setPageSize(rechargeSearchRequest.getPageSize());
        pageRspData.setTotal(page.getTotal());
        return pageRspData;
    }

    public TransferRecord toRechargeResponse(TRechargeWithdrawOrder tRechargeOrder) {
        TransferRecord result = new TransferRecord();
        //result.setSignedAccount(tRechargeOrder.get);
        result.setAmount(tRechargeOrder.getAmount());
        result.setUserId(tRechargeOrder.getUserId());
        result.setStatus(tRechargeOrder.getStatus());
        result.setCreateTime(tRechargeOrder.getCreateTime());
        result.setOrderId(tRechargeOrder.getOrderId());
        result.setSignedAccount(tRechargeOrder.getSignAccountId());
        result.setSignedBank(tRechargeOrder.getSignChannel());
        result.setType("IN");
        return result;
    }

	

	public void checkRechargeRequestParamp(TransferRequest rechargeRequest) {
		if (rechargeRequest.getAmount() == null
				|| rechargeRequest.getAmount() < 1) {
			throw new IllegalArgumentException(
					"parameter amount can not be null");
		}
		if (rechargeRequest.getPassword() == null) {
			throw new IllegalArgumentException(
					"parameter Password can not be null");
		}
		if (rechargeRequest.getSerialNo() == null) {
			throw new IllegalArgumentException(
					"parameter SerialNo can not be null");
		}
		if (rechargeRequest.getUserId() == null) {
			throw new IllegalArgumentException(
					"parameter UserId can not be null");
		}
      /**  if (StringUtils.isBlank(rechargeRequest.getOrgId())) {
            throw new IllegalArgumentException("parameter OrgId can not be null");
        }**/
	}
	
}
