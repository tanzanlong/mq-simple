package com.baibei.accountservice.account.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.account.dto.BalancetypeRoleMap;
import com.baibei.account.dto.RegInfoResponse;
import com.baibei.account.dto.request.ConfirmPasswordRequest;
import com.baibei.account.dto.request.CreateOrgAccountRequest;
import com.baibei.account.dto.request.CreatePersonalAccountRequest;
import com.baibei.account.dto.request.VerifyCreateAccountRequest;
import com.baibei.account.exception.AccountException;
import com.baibei.account.provider.AccountProvider;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.business.CustomerBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.util.CustomerUtil;
import com.baibei.accountservice.config.DynamicConfig;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TAccountPasswordMapper;
import com.baibei.accountservice.dao.TCustomerMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountBalanceExample;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TAccountPassword;
import com.baibei.accountservice.model.TAccountPasswordExample;
import com.baibei.accountservice.model.TCustomer;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.constant.PayCenterConstant;
import com.baibei.accountservice.paycenter.dto.base.BaseResponse;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.paycenter.utill.MD5;
import com.baibei.accountservice.rocketmq.RocketMQUtils;
import com.baibei.accountservice.vo.ch.XmodeCustomerRegInfo;
import com.baibei.accountservice.vo.ch.XmodeOrgRegInfo;
import com.baibei.product.dto.ProductInfoDto;
import com.baibei.product.provider.ProductQueryProvider;
import com.baibei.product.response.ListResponse;
import com.baibei.push.server.provider.SmsPushProvider;
import com.baibei.repository.request.init.AddUserInventoryAccountRequest;
import com.baibei.repository.service.init.UserInventoryInitService;
import com.baibei.user.api.service.common.CommonUserService;

@Service
@Slf4j
public class AccountProviderImpl implements AccountProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(AccountProviderImpl.class);
   

    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;

    @Autowired
    TAccountMapper tAccountMapper;

    @Autowired
    TAccountPasswordMapper tAccountPasswordMapper;

    @Autowired
    TCustomerMapper tCustomerMapper;

    @Autowired
    AccountBusiness accountBusiness;

    @Autowired
    CustomerBusiness customerBusiness;
    
    @Autowired
    RocketMQUtils  rocketMQUtils;
    
    @Autowired
    SmsPushProvider smsPushProvider;
    
    @Autowired
    UserInventoryInitService userInventoryInitService;
    
    @Autowired
    ProductQueryProvider productQueryProvider;
    
    @Autowired
    CommonUserService commonUserService;
    
    @Autowired
    DailyTaskConfig dailyTaskConfig;
    
    @Autowired
    DynamicConfig dynamicConfig;
 
    @Transactional(propagation=Propagation.REQUIRED)
    public RegInfoResponse createPersonalAccount(CreatePersonalAccountRequest customerRegInfo)
            throws AccountException {
        RegInfoResponse regInfoResponse = this.createPersonalAccountKernal(customerRegInfo);
        if (regInfoResponse != null && regInfoResponse.getAccountId() > 0) {
            UserSignMqBody userSignMqBody = new UserSignMqBody();
            userSignMqBody.setUserId(customerRegInfo.getUserId());
            userSignMqBody.setBankSignStatus(true);
            sendCreateAccountSuccessMq(customerRegInfo.getUserId(),JSON.toJSONString(userSignMqBody));
           // initInventory(customerRegInfo.getUserId(),UserType.OTHER);
            commonUserService.setOpenAccountFlag(Long.valueOf(customerRegInfo.getUserId()));
        }

        return regInfoResponse;

    }
    
    

    /**
     * @param userId
     * @param userType 
     */
    public void initInventory(String userId, String userType) {
        try {
            ListResponse<ProductInfoDto> prListResponse =
                    productQueryProvider.queryProductAllList();
            if (prListResponse == null || prListResponse.getList() == null
                    || prListResponse.getList().size() <= 0) {
                return;
            }
            logger.info(" initInventory prListResponse size:{} ",prListResponse.getList().size());
            List<ProductInfoDto> products = prListResponse.getList();

            for (ProductInfoDto productInfoDto : products) {
                AddUserInventoryAccountRequest addUserInventoryRequest =
                        new AddUserInventoryAccountRequest();
                addUserInventoryRequest.setUserID(Long.parseLong(userId));
                addUserInventoryRequest.setUserType(userType);
                addUserInventoryRequest.setProductCode(productInfoDto.getProductCode());
                logger.info(" initInventory ProductCode:{} ",productInfoDto.getProductCode());
                userInventoryInitService.addUserInventoryAccount(addUserInventoryRequest);
            }
            // 发送密码短信

        } catch (Exception e) {
            logger.error(" initInventory userId:{} initInventory  exception:{}  ", userId, e);
        }

    }

    /**
     * 开户成功发送通知
     * 
     * @param userId
     * @param content
     */
    public void sendCreateAccountSuccessMq(String userId, String content) {
        try {
            rocketMQUtils.send(Constants.MQ_TOPIC_CUSTOMER_SIGN, null, null, content);
            logger.info("userId:{} send sendCreateAccountSuccessMq {} success", userId, content);
        } catch (Exception e) {
            logger.error(" createOrgAccount userId:{} send msg exception:{}  ", userId, e);
        }
    }

    
   // @Transactional(propagation=Propagation.REQUIRED) //Propagation.REQUIRED
    public RegInfoResponse  createAccountKernal(CreateAccountRequest createAccountRequest) throws AccountException{
        String idType = createAccountRequest.getIdType();
        String idCode = createAccountRequest.getIdCode();
        String name = createAccountRequest.getName();
        String userId = createAccountRequest.getUserId();
        String topOrgId = createAccountRequest.getTopOrgId();
        String password = createAccountRequest.getPassword();
        String userType=createAccountRequest.getUserType();
        String orgType=createAccountRequest.getOrgType();
        
        Date now=new Date();
        
        
        // 账户初始化
        List<String> balanceTypeList = BalancetypeRoleMap.balanceTypeDiction.get(orgType);
        
        if(CollectionUtils.isEmpty(balanceTypeList)){
            throw new AccountException("invalide usertype"); 
        }
        
        
        // 2 客户操作
        TCustomer tCustomer =
                customerBusiness.qryCustomerByCert(idType,idCode);
        if (tCustomer != null) {// 相同证件客户已存在,不需要再调用实名认证接口
            if (!tCustomer.getCustomerName().equals(name)) {// 姓名不一致
                throw new AccountException("客户姓名与证件号码信息不一致");
            }
        } else {// 调用实名认证接口
            if (doRealNameAnalyze("") == false) {
                throw new AccountException("实名认证不通过");
            }
          /**
           * 初始化客户信息
           */
         
          this.initCustomer(now, name, idCode, idType);
          tCustomer =
                  customerBusiness.qryCustomerByCert(idType,idCode);
        }
        RegInfoResponse response = new RegInfoResponse();
        // 3 账户操作
        TAccount tAccount = accountBusiness.qryAccountByUserId(userId);
        if (tAccount != null) {
            // 响应
            response.setAccountId(tAccount.getAccountId());
            return response;
        }
        BaseResponse<Long> remoteResult=null;
        
        
        if (!"".equals(orgType) && "PERSONAL".equals(orgType)) {
            try {
                XmodeCustomerRegInfo xmodeCustomerRegInfo = new XmodeCustomerRegInfo();
                xmodeCustomerRegInfo.setBankCard("");
                xmodeCustomerRegInfo.setBankCode("");
                xmodeCustomerRegInfo.setBusinessType("CH");
                xmodeCustomerRegInfo.setIdCode(createAccountRequest.getIdCode());
                xmodeCustomerRegInfo.setIdType(createAccountRequest.getIdType());
                xmodeCustomerRegInfo.setName(createAccountRequest.getName());
                xmodeCustomerRegInfo.setOrgId("");
                xmodeCustomerRegInfo.setPhone("");
                xmodeCustomerRegInfo.setUserId(createAccountRequest.getUserId());
                remoteResult = callPersonalCreateAccountInterface(xmodeCustomerRegInfo);
            } catch (Exception e) {
                logger.error("createPersonalAccountKernal userId:{} call sttlement exception:{}  ",
                        userId, e);
                throw new AccountException("开户失败");
            }

        } else {
            try {
                XmodeOrgRegInfo xmodeOrgRegInfo = new XmodeOrgRegInfo();
                xmodeOrgRegInfo.setBankCard("");
                xmodeOrgRegInfo.setBankCode("");
                xmodeOrgRegInfo.setBusinessType("CH");
                xmodeOrgRegInfo.setIdCode(idCode);
                xmodeOrgRegInfo.setIdType(idType);
                xmodeOrgRegInfo.setName(name);
                xmodeOrgRegInfo.setOrgId("");
                xmodeOrgRegInfo.setOrgName("");
                xmodeOrgRegInfo.setPhone("");
                xmodeOrgRegInfo.setRole(2);
                xmodeOrgRegInfo.setUserId(createAccountRequest.getUserId());
                remoteResult = callOrgCreateAccountInterface(xmodeOrgRegInfo);
            } catch (Exception e) {
                logger.error("createPersonalAccountKernal userId:{} call sttlement exception:{}  ",
                        userId, e);
                throw new AccountException("开户失败");
            }
        }
        
        
        logger.info(" createAccount  remoteResult :{} ",JSON.toJSONString(remoteResult));
       //accountService.createAccount("CH", customerRegInfo.getUserId());
       if(remoteResult==null||BaseResponse.RC_SUCCESS!=remoteResult.getRc()||remoteResult.getData()==null||remoteResult.getData()<=0){
           throw new AccountException("开户失败");
       }
        Long accountId=remoteResult.getData();

        //String password =  password;//customerRegInfo.getPassword();
        initAccount(now, balanceTypeList, tCustomer.getCustomerId(),
                userId, password,accountId,userType,topOrgId,orgType);       
        // 账户余额
        initUserBalanceTypes(now, userId,  accountId,balanceTypeList,createAccountRequest.getBusinessType());
        /**
         * 密码初始化
         */
        initPassword( now,  accountId,  password,  userId);
        // 响应
        response.setAccountId(accountId);
        return response;
    }
    
    
       
    @Transactional(propagation=Propagation.REQUIRED) //Propagation.REQUIRED
    public RegInfoResponse createPersonalAccountKernal(CreatePersonalAccountRequest customerRegInfo)
            throws AccountException {
        // 1 参数判断
        checkParam(customerRegInfo);
        String idType = "1";
        // 2 客户操作
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setIdCode(customerRegInfo.getIdCode());
        createAccountRequest.setIdType(idType);
        createAccountRequest.setName(customerRegInfo.getName());
        createAccountRequest.setOrgType("PERSONAL");
        createAccountRequest.setPassword(customerRegInfo.getPassword());
        createAccountRequest.setTopOrgId(customerRegInfo.getTopOrgId());
        createAccountRequest.setUserId(customerRegInfo.getUserId());
        createAccountRequest.setUserType("PERSONAL");
        createAccountRequest.setBusinessType("CH");
        return this.createAccountKernal(createAccountRequest);
    }

    
    public RegInfoResponse createOrgAccountKernal(CreateOrgAccountRequest orgRegInfo) throws AccountException{
        log.info("createOrgAccount  CreateOrgAccountRequest:{} ",JSON.toJSONString(orgRegInfo));
        // 1 参数判断
        checkOrgParam(orgRegInfo);
        
        List<String> balanceTypeList =
                BalancetypeRoleMap.balanceTypeDiction.get(orgRegInfo.getOrgType());
        if(CollectionUtils.isEmpty(balanceTypeList)){
            throw new AccountException("invalide usertype"); 
        }
        
        String idType="52";
        String password = "";
        if ("true".equalsIgnoreCase(dynamicConfig.getSwitchSystemTest())) {
            password = "123456";
        } else {
            password = CustomerUtil.getRandomStringByLen(6);
        }
       
        log.info("  createOrgAccountKernal password:{}",password);
        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setIdCode(orgRegInfo.getIdCode());
        createAccountRequest.setIdType(idType);
        createAccountRequest.setName(orgRegInfo.getName());
        createAccountRequest.setOrgType(orgRegInfo.getOrgType());
        createAccountRequest.setPassword(password);
        createAccountRequest.setTopOrgId("");
        createAccountRequest.setUserId(orgRegInfo.getUserId());
        createAccountRequest.setUserType("ORG");
        createAccountRequest.setBusinessType("CH");
        try{
            doSendPwdSmsMessage(orgRegInfo.getMobile(), password);
        }catch(Exception e){
            logger.error("createOrgAccountKernal doSendPwdSmsMessage exception:{} ",e);
        }
       
        return this.createAccountKernal(createAccountRequest);
    
    }
    
    

    @Transactional(propagation = Propagation.REQUIRED)
    public void initUserBalanceTypes(Date now, String userId, Long accountId,
            List<String> balanceTypes,String businessType) {
        for (int index = 0; index < balanceTypes.size(); index++) {
            String balanceType = balanceTypes.get(index);
            this.initBalanceType(now, userId, accountId, balanceType,businessType);
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void initBalanceType(Date now, String userId, Long accountId, String balanceType,String businessType) {
        TAccountBalance tAccountBalance = new TAccountBalance();
        tAccountBalance.setAccountId(accountId);
        tAccountBalance.setAmount(0L);
        tAccountBalance.setBalanceType(balanceType);
        tAccountBalance.setCreateTime(now);
        tAccountBalance.setUpdateTime(now);
        tAccountBalance.setUserId(userId);
        tAccountBalance.setBusinessType(businessType);
        tAccountBalance.setVersion(1L);
        tAccountBalanceMapper.insert(tAccountBalance);
    }
    
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void initCustomer(Date now, String name, String idCode, String idType) {
        TCustomer tCustomer = new TCustomer();
        // Date date = new Date();
        tCustomer.setCreateTime(now);
        // tCustomer.setCustomerId(customerId);
        tCustomer.setCustomerName(name);
        tCustomer.setIdCode(idCode);
        tCustomer.setIdType(idType);
        tCustomer.setIsDel(Constants.ACCOUNT_ISDELETE_NO);
        tCustomer.setUpdateTime(now);
        tCustomerMapper.insert(tCustomer);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void initPassword(Date now, Long accountId, String password, String userId) {
        // 账户密码
        TAccountPassword tAccountPassword = new TAccountPassword();
        tAccountPassword.setAccountId(accountId);
        tAccountPassword.setCreateTime(now);
        String salt = UUID.randomUUID().toString().toLowerCase();
        String md5Pwd = MD5.getHashString(password + salt);
        tAccountPassword.setSalt(salt);
        tAccountPassword.setPassword(md5Pwd);
        tAccountPassword.setUpdateTime(now);
        tAccountPassword.setUserId(userId);
        tAccountPassword.setErrorCount(0);
        tAccountPasswordMapper.insert(tAccountPassword);
    }

    
    // 账户初始化
    @Transactional(propagation=Propagation.REQUIRED)
    public Long initAccount( Date now ,List<String> balanceTypeList, Long customerId,
            String userId, String password,Long accountId,String userType,String topOrgId,String orgType) {
        // 账户
        TAccount tAccount = new TAccount();
        tAccount.setAccountId(accountId);
        tAccount.setCreateTime(now);
        tAccount.setCustomerId(customerId);
        tAccount.setIsDel(0);
        tAccount.setIsSign(1);
        tAccount.setSignChannel("");
        tAccount.setUpdateTime(now);
        tAccount.setUserId(userId);
        tAccount.setUserType(userType);
        tAccount.setTopOrgId(topOrgId);
        tAccount.setOrgType(orgType);
        tAccount.setSignChannel("");
        tAccount.setSignAccountId("");
        tAccountMapper.insert(tAccount);
        return tAccount.getAccountId();
    }

    private void doSendPwdSmsMessage(String phone, String pwd) {
        // TODO 调用短信发送接口发送账户密码到指定手机号
      //  String appKey="";
        List<String> mobiles=new ArrayList<String>();
        mobiles.add(phone);
        String message="你好 !你的资金密码是:"+pwd;
        String appKey="APP_NEW_TRADE";
       smsPushProvider.sendMsg(appKey, mobiles, message);
    }

    private boolean doRealNameAnalyze(String idType) {
        // TODO 调用四要素验证接口
        return true;
    }

    private void checkParam(CreatePersonalAccountRequest customerRegInfo) {
       if (StringUtils.isBlank(customerRegInfo.getTopOrgId())) {
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getIdCode())) {
            throw new IllegalArgumentException("parameter idCode can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getName())) {
            throw new IllegalArgumentException("parameter name can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getMobile())) {
            throw new IllegalArgumentException("parameter phone can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getUserId())) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    }
    
    
    private void checkOrgParam(CreateOrgAccountRequest customerRegInfo) {
       /** if (StringUtils.isBlank(customerRegInfo.getBusinessType())) {
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getIdType())) {
            throw new IllegalArgumentException("parameter idType can not be blank");
        }**/
        if (StringUtils.isBlank(customerRegInfo.getIdCode())) {
            throw new IllegalArgumentException("parameter idCode can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getName())) {
            throw new IllegalArgumentException("parameter name can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getMobile())) {
            throw new IllegalArgumentException("parameter phone can not be blank");
        }
        if (StringUtils.isBlank(customerRegInfo.getUserId())) {
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    }
    
    
    @Override
    public RegInfoResponse createOrgAccount(CreateOrgAccountRequest orgRegInfo)
            throws AccountException {
        RegInfoResponse regInfoResponse = this.createOrgAccountKernal(orgRegInfo);
        if (regInfoResponse != null && regInfoResponse.getAccountId() > 0) {
            UserSignMqBody userSignMqBody = new UserSignMqBody();
            userSignMqBody.setUserId(orgRegInfo.getUserId());
            userSignMqBody.setBankSignStatus(true);
            sendCreateAccountSuccessMq(orgRegInfo.getUserId(), JSON.toJSONString(userSignMqBody));
            // doSendPwdSmsMessage(orgRegInfo.getMobile(), orgRegInfo.getPassword());
            commonUserService.setOpenAccountFlag(Long.valueOf(orgRegInfo.getUserId()));
        /**    String orgType = orgRegInfo.getOrgType();
            if (CreateOrgAccountRequest.TYPE_SPECIAL.equals(orgType)) {
                initInventory(orgRegInfo.getUserId(), UserType.ETFS);
            } else if (orgType.equals(CreateOrgAccountRequest.TYPE_INDUSTRY)) {
                initInventory(orgRegInfo.getUserId(), UserType.INDUSTRY);
            } else {
                initInventory(orgRegInfo.getUserId(), UserType.OTHER);
            }**/

        }
        return regInfoResponse;

    }


    public RegInfoResponse createOrgAccountKernaldemo(CreateOrgAccountRequest orgRegInfo) throws AccountException{
        log.info("createOrgAccount  CreateOrgAccountRequest:{} ",JSON.toJSONString(orgRegInfo));
        // 1 参数判断
        checkOrgParam(orgRegInfo);
        
        List<String> balanceTypeList =
                BalancetypeRoleMap.balanceTypeDiction.get(orgRegInfo.getOrgType());
        if(CollectionUtils.isEmpty(balanceTypeList)){
            throw new AccountException("invalide usertype"); 
        }
        
        String idType="1";
      //  String businessType="CH";
        Date now = new Date();
        String userId=orgRegInfo.getUserId();
        // 2 客户操作
        TCustomer tCustomer =
                customerBusiness.qryCustomerByCert(idType, orgRegInfo.getIdCode());
        
        BaseResponse<Long> remoteResult=null;
        try{
            XmodeOrgRegInfo xmodeOrgRegInfo = new XmodeOrgRegInfo();
            xmodeOrgRegInfo.setBankCard("");
            xmodeOrgRegInfo.setBankCode("");
            xmodeOrgRegInfo.setBusinessType("CH");
            xmodeOrgRegInfo.setIdCode(orgRegInfo.getIdCode());
            xmodeOrgRegInfo.setIdType("");
            xmodeOrgRegInfo.setName(orgRegInfo.getName());
            xmodeOrgRegInfo.setOrgId("");
            xmodeOrgRegInfo.setOrgName("");
            xmodeOrgRegInfo.setPhone("");
            xmodeOrgRegInfo.setRole(CreateOrgAccountRequest.getSettlementOrgType(orgRegInfo.getOrgType()));
            xmodeOrgRegInfo.setUserId(orgRegInfo.getUserId());
            remoteResult= callOrgCreateAccountInterface(xmodeOrgRegInfo); 
        }catch(Exception e){
            logger.error("createPersonalAccountKernal userId:{} call sttlement exception:{}  ",userId,e);
            throw new AccountException("开户失败");
        }
        
       //accountService.createAccount("CH", customerRegInfo.getUserId());
       if(remoteResult==null||BaseResponse.RC_SUCCESS!=remoteResult.getRc()||remoteResult.getData()==null||remoteResult.getData()<=0){
           throw new AccountException("开户失败");
       }
        
        
         Long accountId=remoteResult.getData();
         
         
        if (tCustomer != null) {// 相同证件客户已存在
            if (!tCustomer.getCustomerName().equals(orgRegInfo.getName())) {// 公司名称与证件号不一致
                throw new AccountException("公司名称与证件号码信息不一致");
            }
        } else {
         
            this.initCustomer(now, orgRegInfo.getName(), orgRegInfo.getIdCode(), idType);
            
        }
        RegInfoResponse response = new RegInfoResponse();
        // 3 账户操作
        TAccount tAccount = accountBusiness.qryAccountByUserId(orgRegInfo.getUserId());
        if (tAccount != null) {
            response.setAccountId(accountId);
            return response;
        }

 
        String password = CustomerUtil.getRandomStringByLen(8);

        initAccount(now, balanceTypeList, tCustomer.getCustomerId(),
                orgRegInfo.getUserId(), password, accountId, "org","",orgRegInfo.getOrgType());
        // 账户余额
        initUserBalanceTypes(now, userId, accountId, balanceTypeList,"CH");
        /**
         * 密码初始化
         */
        initPassword(now, accountId, password, userId);

        // 响应
       
        response.setAccountId(accountId);
        return response;
    }


    @Override
    public Boolean deleteAccount(String userId) throws AccountException {
        Boolean isSucess = this.deleteAccountKernal(userId);
        if (isSucess != null && isSucess.booleanValue() == true) {
            UserSignMqBody userSignMqBody = new UserSignMqBody();
            userSignMqBody.setUserId(userId);
            userSignMqBody.setBankSignStatus(false);
            sendCreateAccountSuccessMq(userId, JSON.toJSONString(userSignMqBody));
        }
        return isSucess;
    }
    
    
    public Boolean  deleteAccountKernal(String userId) throws AccountException {
        Boolean isSuccess=false;
        // 1 业务判断
        TAccount tAccount = accountBusiness.qryAccountByUserId(userId);
        if (tAccount == null) {
            throw new IllegalArgumentException("账户不存在");
        }
        if (tAccount.getIsDel() == 1) {
            isSuccess = true;
            return isSuccess;
        }
        if (tAccount.getIsSign() == 1) {
            throw new IllegalArgumentException("账户未解约，请先解约");
        }
        
        /**
         * 余额检查  可用：AVALIABLE，冻结：FREEZON，融资：LOAN，其它应付款：UNPAY'
         */
        TAccountBalanceExample tAccountBalanceExample=new TAccountBalanceExample();
        tAccountBalanceExample.createCriteria().andAccountIdEqualTo(tAccount.getAccountId());
        List<TAccountBalance> tAccountBalances=  tAccountBalanceMapper.selectByExample(tAccountBalanceExample);
        for (TAccountBalance tAccountBalance : tAccountBalances) {
            if(tAccountBalance.getAmount()!=null&&tAccountBalance.getAmount()>0){
                return isSuccess;
            }
        }
        /**
         * TODO 有融货是否不让注销 ？
         */
        
        
        BaseResponse<Boolean> remoteResult=null;
        try{
            remoteResult= callWrittenOffAccountInterface(userId); 
        }catch(Exception e){
            logger.error("callWrittenOffAccountInterface userId:{} call sttlement exception:{}  ",userId,e);
            throw new AccountException("开户失败");
        }
        
       //accountService.createAccount("CH", customerRegInfo.getUserId());
       if(remoteResult==null||BaseResponse.RC_SUCCESS!=remoteResult.getRc()||remoteResult.getData()==null||remoteResult.getData()!=true){
           throw new AccountException("开户失败");
       }
        // 2 标识删除
        tAccount.setIsDel(1);
        tAccount.setUpdateTime(new Date());
        return tAccountMapper.updateByPrimaryKey(tAccount) > 0;
    }

    
    
    
    @Override
    public Boolean modifyPassword(String userId, String oldPassword, String newPassword) {
        return accountBusiness.modifyPassword(userId, oldPassword, newPassword);
    }

    @Override
    public Boolean resetPassword(String userId, String password) {
        return accountBusiness.modifyPassword(userId, password);
    }

    @Override
    public Boolean resetPasswordBySms(String userId,String moblie) {
        return accountBusiness.resetPassword(userId,moblie);
    }
    
    
    
    private BaseResponse<Long> callPersonalCreateAccountInterface(XmodeCustomerRegInfo xmodeCustomerRegInfo){
        BaseResponse<Long> result=new BaseResponse<Long>();
       //String url=acccountConfig.getCustomerPersonCreateUrl();
       String url=dailyTaskConfig.getSettlementCustomerBaseUrl()+"/xmodelaccount/createaccount";
       String json= this.callCreatePersonalAccountInterface(xmodeCustomerRegInfo, url, "");
       JSONObject responseJson=JSON.parseObject(json);
       Integer rc=responseJson.getInteger("rc")==null?-1:responseJson.getInteger("rc");
       result.setRc(rc);
       if(BaseResponse.RC_SUCCESS==rc){
           Long accountId=responseJson.getLong("data");
           result.setData(accountId);
       }
       
       return result;
    }

    
    private BaseResponse<Long> callOrgCreateAccountInterface(XmodeOrgRegInfo xmodeOrgRegInfo){
        BaseResponse<Long> result=new BaseResponse<Long>();
      // String url=acccountConfig.getCustomerOrgCreateUrl();
       String url=dailyTaskConfig.getSettlementCustomerBaseUrl()+"/xmodelaccount/createorgaccount";
       String json= this.callCreateOrgAccountInterface(xmodeOrgRegInfo, url, "");
       JSONObject responseJson=JSON.parseObject(json);
       Integer rc=responseJson.getInteger("rc")==null?-1:responseJson.getInteger("rc");
       result.setRc(rc);
       if(BaseResponse.RC_SUCCESS==rc){
           Long accountId=responseJson.getLong("data");
           result.setData(accountId);
       }
       return result;
    }
    
    private String callCreatePersonalAccountInterface(XmodeCustomerRegInfo xmodeCustomerRegInfo, String url, String appSecret){
        String message = JSON.toJSONString(xmodeCustomerRegInfo);
        return this.callPostInterface(message, url, appSecret);
    }
    
    private String callCreateOrgAccountInterface(XmodeOrgRegInfo xmodeOrgRegInfo, String url, String appSecret){
        String message = JSON.toJSONString(xmodeOrgRegInfo);
        return this.callPostInterface(message, url, appSecret);
    }
    
    
    private BaseResponse<Boolean> callWrittenOffAccountInterface(String userId){
        BaseResponse<Boolean> result=new BaseResponse<Boolean>();
      // String url=acccountConfig.getCustomerWrittenOffUrl()+"/"+userId;
       String url=dailyTaskConfig.getSettlementCustomerBaseUrl()+"/xmodelaccount/dropaccount/CH/"+userId;
       String json= this.callWrittenOffAccountInterface(JSON.parse(userId).toString(),  url, ""); 
       JSONObject responseJson=JSON.parseObject(json);
       Integer rc=responseJson.getInteger("rc")==null?-1:responseJson.getInteger("rc");
       result.setRc(rc);
       if(BaseResponse.RC_SUCCESS==rc){
           Boolean isSuccess=responseJson.getBoolean("data");
           result.setData(isSuccess);
       }
       return result;
    }
    
    private String callWrittenOffAccountInterface(String userId, String url, String appSecret){
        Map<String, String> params=new HashMap<String, String>();
        params.put("userId", userId);
        return this.callGetInterface(params, url, appSecret);
    }
    
    
    //调用业务系统提供的入金通知接口
    private String callGetInterface(  Map<String, String> params,String url, String appSecret){
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
    
    //调用业务系统提供的入金通知接口
    private String callPostInterface( String json,String url, String appSecret){
        Map<String, String> headParams = new HashMap<String, String>();
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        String html = "";
        try {
            logger.info("post {} to url {}", json, url);
            html = HttpClientUtils.doPost(url, headParams, json);
        } catch (Exception e) {
            logger.error(" callInterface url:{} exception:{}",url,e);
           throw new IllegalArgumentException(e.getMessage());
        }
        return html;
    }
    
    
    
    
    @Override
    public Boolean verifyCreateAccountMsg(VerifyCreateAccountRequest verifyCreateAccountRequest) {
        if(verifyCreateAccountRequest==null){
            return false;
        }
        boolean isCanCreate = false;
        Boolean isOrg = verifyCreateAccountRequest.getIfOrg();
        String idType = "";
        String idCode = verifyCreateAccountRequest.getIdCode();
        String realName = verifyCreateAccountRequest.getRealName();
        if (isOrg == true) {
            idType = "52";
        } else if (isOrg == false) {
            idType = "1";
        } else {
            return isCanCreate;
        }
        TCustomer tCustomer = customerBusiness.qryCustomerByCert(idType, idCode);
        if (tCustomer != null) {// 相同证件客户已存在,不需要再调用实名认证接口
            if (!tCustomer.getCustomerName().equals(realName)) {// 姓名不一致
                return isCanCreate;
            }
        } else {// 调用实名认证接口
            if (doRealNameAnalyze("") == false) {
                return isCanCreate;
            }
        }
        isCanCreate = true;
        return isCanCreate;
    }


    
    

    @Override
    public Boolean confirPassWord(ConfirmPasswordRequest confirmPasswordRequest) {
        if(confirmPasswordRequest==null){
            throw new IllegalArgumentException("parameter  can not be null");
        }
        if(confirmPasswordRequest.getPassword()==null){
            throw new IllegalArgumentException("parameter Password can not be null");
        }
       if(confirmPasswordRequest.getUserId()==null){
           throw new IllegalArgumentException("parameter UserId can not be null");
        }
       TAccount tAccount= this.queryAccountByUserId(confirmPasswordRequest.getUserId());
      
       if(tAccount==null){
           throw new IllegalArgumentException("parameter UserId can not be null");  
       }
       boolean isPwdValid = isPasswordValid(tAccount.getAccountId(), confirmPasswordRequest.getPassword());
      return isPwdValid;
    }

    
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
        if (!com.baibei.accountservice.util.MD5.sign(inputPassword, tAccountPassword.getSalt(), "UTF-8").equals(
                tAccountPassword.getPassword())) {
            logger.debug(" isPasswordValid password wrong  ");
            return isValid;
        }
        isValid = true;
        return isValid;
    }
    


    static class CreatePersonalAccountResponse{
        @Setter
        @Getter
        String userId;
        @Setter
        @Getter
        String businessType;
    }
    
    
    static class CreateOrgAccountResponse{
        @Setter
        @Getter
        String userId;
        @Setter
        @Getter
        String businessType;
        @Setter
        @Getter
        String orgType;
    }
    
    static class UserSignMqBody {
        @Setter
        @Getter
        String userId;
        @Setter
        @Getter
        Boolean bankSignStatus;
    }
    
    
    
    static class CreateAccountRequest{
        @Setter
        @Getter
        String idType;
        @Setter
        @Getter
        String idCode;
        @Setter
        @Getter
        String name;
        @Setter
        @Getter
        String userId;
        @Setter
        @Getter
        String topOrgId;
        @Setter
        @Getter
        String password;
        @Setter
        @Getter
        String userType;
        @Setter
        @Getter
        String orgType;
        @Setter
        @Getter
        String businessType;
    }
}
