package com.baibei.accountservice.controller.cb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baibei.accountservice.account.business.AccountBusiness;
import com.baibei.accountservice.account.comm.Constants;
import com.baibei.accountservice.account.vo.ValidatePasswordResult;
import com.baibei.accountservice.dao.TAccountBalanceMapper;
import com.baibei.accountservice.dao.TAccountMapper;
import com.baibei.accountservice.dao.TCustomerMapper;
import com.baibei.accountservice.model.TAccount;
import com.baibei.accountservice.model.TAccountBalance;
import com.baibei.accountservice.model.TAccountExample;
import com.baibei.accountservice.model.TCustomer;
import com.baibei.accountservice.model.TCustomerExample;
import com.baibei.accountservice.paycenter.config.DailyTaskConfig;
import com.baibei.accountservice.paycenter.dto.BaseResponse;
import com.baibei.accountservice.paycenter.utill.HttpClientUtils;
import com.baibei.accountservice.util.IDGenerator;
import com.baibei.accountservice.util.RspUtils;
import com.baibei.accountservice.vo.cb.CbChangePasswordReq;
import com.baibei.accountservice.vo.cb.CbCreatePasswordReq;
import com.baibei.accountservice.vo.cb.CreateAccount;
import com.baibei.accountservice.vo.cb.CreateOrgAccount;
import com.baibei.accountservice.vo.cb.UpdateAccountInfo;

import lombok.extern.slf4j.Slf4j;

@RestController
@EnableAutoConfiguration
@RequestMapping("/account/cbcustomer")
@Slf4j
public class CustomerCbImplController {

    @Autowired
    DailyTaskConfig dailyTaskConfig;
    
    @Autowired
    TAccountMapper tAccountMapper;
    
    @Autowired
    TCustomerMapper tCustomerMapper;
    
    @Autowired
    AccountBusiness accountBusiness;
    
    @Autowired
    TAccountBalanceMapper tAccountBalanceMapper;

    /**
     * 个人开户
     * @param closePosition
     * @return
     */
    @RequestMapping(value = "/createAccount")
    public BaseResponse<Boolean> createAccount(@RequestBody CreateAccount createAccount){
        try{
            //参数检查
            checkParam(createAccount);
           
            //检查该用户是否已开户，如是，直接返回成功（幂等）
            if(isAccountAlreadyExists(createAccount.getUserId())){
                return RspUtils.success(true);
            }
            
            //调用清结算个人开户接口并取得账户ID
            long accountId = callCeretePersonalAccountInterface(createAccount);
            //生成customerID
            long customerId = IDGenerator.next();
            //保存客户和账户
            saveCustomerAndAccount(createAccount, customerId, accountId);
            return RspUtils.success(true);
        }catch(Exception e){
            log.info(" 开户失败 :{}",e.getMessage());
            return RspUtils.error("开户失败:" + e.getMessage());
        }
    }
    
    /**
     * 组织机构开户
     * @param closePosition
     * @return
     */
    @RequestMapping(value = "/createOrgAccount")
    public BaseResponse<Boolean> createOrgAccount(@RequestBody CreateOrgAccount createOrgAccount){
        try{
            //参数检查
            checkParam(createOrgAccount);
           
            //检查该用户是否已开户，如是，直接返回成功（幂等）
            if(isAccountAlreadyExists(createOrgAccount.getUserId())){
                return RspUtils.success(true);
            }
            
            //调用清结算个人开户接口并取得账户ID
            long accountId = callCereteOrgAccountInterface(createOrgAccount);
            //生成customerID
            long customerId = IDGenerator.next();
            //保存客户和账户
            saveCustomerAndAccount(createOrgAccount, customerId, accountId);
            return RspUtils.success(true);
        }catch(Exception e){
            return RspUtils.error(e.getMessage());
        }
    }
    
    //参数检查
    private void checkParam(CreateAccount createAccount){
        if(createAccount.getBankCard() == null){
            throw new IllegalArgumentException("parameter bankCard can not be null");
        }
        if(createAccount.getBankCode() == null){
            throw new IllegalArgumentException("parameter bankCode can not be null");
        }
        if(StringUtils.isBlank(createAccount.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(createAccount.getIdCode() == null){
            throw new IllegalArgumentException("parameter idCode can not be null");
        }
        if(createAccount.getIdType() == null){
            throw new IllegalArgumentException("parameter idType can not be null");
        }
        if(createAccount.getName() == null){
            throw new IllegalArgumentException("parameter name can not be null");
        }
        if(createAccount.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(createAccount.getPhone() == null){
            throw new IllegalArgumentException("parameter phone can not be null");
        }
        if(StringUtils.isBlank(createAccount.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be null");
        }
    }
    
    //参数检查
    private void checkParam(CreateOrgAccount createAccount){
        if(createAccount.getBankCard() == null){
            throw new IllegalArgumentException("parameter bankCard can not be null");
        }
        if(createAccount.getBankCode() == null){
            throw new IllegalArgumentException("parameter bankCode can not be null");
        }
        if(StringUtils.isBlank(createAccount.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(createAccount.getIdCode() == null){
            throw new IllegalArgumentException("parameter idCode can not be null");
        }
        if(createAccount.getIdType() == null){
            throw new IllegalArgumentException("parameter idType can not be null");
        }
        if(createAccount.getName() == null){
            throw new IllegalArgumentException("parameter name can not be null");
        }
        if(createAccount.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(createAccount.getPhone() == null){
            throw new IllegalArgumentException("parameter phone can not be null");
        }
        if(StringUtils.isBlank(createAccount.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be null");
        }
        if(createAccount.getOrgId() == null){
            throw new IllegalArgumentException("parameter orgId can not be null");
        }
        if(createAccount.getOrgName() == null){
            throw new IllegalArgumentException("parameter orgName can not be null");
        }
        if(createAccount.getRole() == null){
            throw new IllegalArgumentException("parameter role can not be null");
        }
    }
    
    //调用清结算个人开户接口
    private Long callCeretePersonalAccountInterface(CreateAccount createAccount){
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getSettlementCustomerBaseUrl() + "/cbaccount/createaccount";
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bankCard", createAccount.getBankCard());
        params.put("bankCode", createAccount.getBankCode());
        params.put("businessType", createAccount.getBusinessType());
        params.put("idType", createAccount.getIdType());
        params.put("idCode", createAccount.getIdCode());
        params.put("name", createAccount.getName());
        params.put("orgId", createAccount.getOrgId());
        params.put("orgName", createAccount.getOrgName()==null?"":createAccount.getOrgName());
        params.put("phone", createAccount.getPhone());
        params.put("userId", createAccount.getUserId());
        String html = "";
        try {
            html = HttpClientUtils.doPost(url, headParams, JSON.toJSONString(params));
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            if(jsonObj.getInteger("rc") != 1){
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }else{
                return jsonObj.getLong("data");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
    
    //调用清结算机构开户接口
    private Long callCereteOrgAccountInterface(CreateOrgAccount createAccount){
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getSettlementCustomerBaseUrl() + "/cbaccount/createorgaccount";
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("bankCard", createAccount.getBankCard());
        params.put("bankCode", createAccount.getBankCode());
        params.put("businessType", createAccount.getBusinessType());
        params.put("idType", createAccount.getIdType());
        params.put("idCode", createAccount.getIdCode());
        params.put("name", createAccount.getName());
        params.put("orgId", createAccount.getOrgId());
        params.put("phone", createAccount.getPhone());
        params.put("userId", createAccount.getUserId());
        params.put("orgId", createAccount.getOrgId());
        params.put("orgName", createAccount.getOrgName());
        params.put("role", createAccount.getRole());
        String html = "";
        try {
            html = HttpClientUtils.doPost(url, headParams, JSON.toJSONString(params));
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            if(jsonObj.getInteger("rc") != 1){
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }else{
                return jsonObj.getLong("data");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
    
    
    //保存客户和账户
    private void saveCustomerAndAccount(CreateAccount createAccount, Long cusomerId, Long accountId){
        Date date = new Date();
        TCustomer tCustomer = new TCustomer();
        tCustomer.setCreateTime(date);
        tCustomer.setCustomerId(cusomerId);
        tCustomer.setCustomerName(createAccount.getName());
        tCustomer.setIdCode(createAccount.getIdCode());
        tCustomer.setIdType(createAccount.getIdType());
        tCustomer.setIsDel(0);
        tCustomer.setUpdateTime(date);
        tCustomerMapper.insert(tCustomer);
        
        TAccount tAccount = new TAccount();
        tAccount.setAccountId(accountId);
        tAccount.setCreateTime(date);
        tAccount.setCustomerId(cusomerId);
        tAccount.setIsDel(0);
        tAccount.setIsSign(0);
        tAccount.setOrgType("");
        tAccount.setSignAccountId("");
        tAccount.setSignChannel("");
        tAccount.setTopOrgId("");
        tAccount.setUpdateTime(date);
        tAccount.setUserId(createAccount.getUserId());
        tAccount.setUserType("PERSONAL");
        tAccountMapper.insert(tAccount);
        
        // 账户初始化
        List<String> personalBalanceType=new ArrayList<String>();
        personalBalanceType.add("AVALIABLE");
        personalBalanceType.add("FREEZON");
        initUserBalanceTypes(date, createAccount.getUserId(),  accountId,personalBalanceType,createAccount.getBusinessType());
    }
    
    //保存客户和账户
    private void saveCustomerAndAccount(CreateOrgAccount createAccount, Long cusomerId, Long accountId){
        Date date = new Date();
        TCustomer tCustomer = new TCustomer();
        tCustomer.setCreateTime(date);
        tCustomer.setCustomerId(cusomerId);
        tCustomer.setCustomerName(createAccount.getName());
        tCustomer.setIdCode(createAccount.getIdCode());
        tCustomer.setIdType(createAccount.getIdType());
        tCustomer.setIsDel(0);
        tCustomer.setUpdateTime(date);
        tCustomerMapper.insert(tCustomer);
        
        TAccount tAccount = new TAccount();
        tAccount.setAccountId(accountId);
        tAccount.setCreateTime(date);
        tAccount.setCustomerId(cusomerId);
        tAccount.setIsDel(0);
        tAccount.setIsSign(0);
        tAccount.setOrgType("");
        tAccount.setSignAccountId("");
        tAccount.setSignChannel("");
        tAccount.setTopOrgId("");
        tAccount.setUpdateTime(date);
        tAccount.setUserId(createAccount.getUserId());
        tAccount.setUserType("ORG");
        tAccountMapper.insert(tAccount);
        
        // 账户初始化
        List<String> baalnceTypeList=new ArrayList<String>();
        baalnceTypeList.add("AVALIABLE");
        baalnceTypeList.add("FREEZON");
        baalnceTypeList.add(Constants.BALANCE_TYPE_TICKET_BONDS);
        initUserBalanceTypes(date, createAccount.getUserId(),  accountId, baalnceTypeList, createAccount.getBusinessType());
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
    
    
    //检查账户是否已存在
    private boolean isAccountAlreadyExists(String userId){
        //检查该用户是否已开户，如是，直接返回成功（幂等）
        TAccountExample example = new TAccountExample();
        example.createCriteria().andUserIdEqualTo(userId);
        List<TAccount> list = tAccountMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return true;
        }
        return false;
    }
    
   /**
    * 销户
    * @param businessType
    * @param userId
    * @return
    */
    @RequestMapping(value = "/dropaccount/{businessType}/{userId}")
    public BaseResponse<Boolean> dropaccount(@PathVariable("businessType") String businessType, @PathVariable("userId") String userId){
        try{
            if(callDropAccountInterface(businessType, userId) == true){//调用接口成功
                TAccountExample example = new TAccountExample();
                example.createCriteria().andUserIdEqualTo(userId);
                List<TAccount> list = tAccountMapper.selectByExample(example);
                if(CollectionUtils.isNotEmpty(list)){
                    for(TAccount tAccount : list){
                        tAccount.setIsDel(1);
                        tAccount.setUpdateTime(new Date());
                        tAccountMapper.updateByPrimaryKey(tAccount);
                    }
                }
                return RspUtils.success(true);
            }
            return RspUtils.success(false);
        }catch(Exception e){
            log.error(e.getMessage());
            return RspUtils.error(e.getMessage());
        }
    }
    
    //调用清结算机构开户接口
    private boolean callDropAccountInterface(String businessType, String userId){
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getSettlementCustomerBaseUrl() + "/cbaccount/dropaccount/" + businessType + "/" + userId;
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        String html = "";
        try {
            html = HttpClientUtils.doGet(url, headParams, new HashMap<String, String>());
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            if(jsonObj.getInteger("rc") != 1){
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }else{
                return jsonObj.getBoolean("data");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
    
    private void checkParam(CbCreatePasswordReq req){
        if(StringUtils.isBlank(req.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(req.getPassword())){
            throw new IllegalArgumentException("parameter password can not be blank");
        }
        if(StringUtils.isBlank(req.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    }
    
    //创建密码
    @RequestMapping(method = RequestMethod.POST, value = "/createpassword")
    public BaseResponse<Boolean> createpassword(@RequestBody CbCreatePasswordReq cbCreatePasswordReq, HttpServletRequest request){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try{
            checkParam(cbCreatePasswordReq);
            response.setRc(BaseResponse.RC_SUCCESS);
            response.setData(accountBusiness.resetOrUpdatePassword(cbCreatePasswordReq.getUserId(), cbCreatePasswordReq.getPassword()));
        }catch(Exception e){
            log.error(e.getMessage());
            response.setRc(BaseResponse.RC_FAIL);
            response.setMsg(e.getMessage());
        }
        return response;
    }
    
    //重置密码
    @RequestMapping(method = RequestMethod.POST, value = "/resetpassword")
    public BaseResponse<Boolean> resetpassword(@RequestBody CbCreatePasswordReq cbCreatePasswordReq, HttpServletRequest request){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try{
            checkParam(cbCreatePasswordReq);
            response.setRc(BaseResponse.RC_SUCCESS);
            response.setData(accountBusiness.resetOrUpdatePassword(cbCreatePasswordReq.getUserId(), cbCreatePasswordReq.getPassword()));
        }catch(Exception e){
            log.error(e.getMessage());
            response.setRc(BaseResponse.RC_FAIL);
            response.setMsg(e.getMessage());
        }
        return response;
    }
    
    //密码检验
    @RequestMapping(method = RequestMethod.POST, value = "/validatepassword")
    public BaseResponse<ValidatePasswordResult> validatepassword(@RequestBody CbCreatePasswordReq cbCreatePasswordReq, HttpServletRequest request){
        BaseResponse<ValidatePasswordResult> response = new BaseResponse<ValidatePasswordResult>();
        try{
            checkParam(cbCreatePasswordReq);
            response.setRc(BaseResponse.RC_SUCCESS);
            response.setData(accountBusiness.validatePassword(cbCreatePasswordReq.getUserId(), cbCreatePasswordReq.getPassword()));
        }catch(Exception e){
            log.error(e.getMessage());
            response.setRc(BaseResponse.RC_FAIL);
            response.setMsg(e.getLocalizedMessage());
        }
        return response;
    }
    
    private void checkParam(CbChangePasswordReq req){
        if(StringUtils.isBlank(req.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(StringUtils.isBlank(req.getNewPassword())){
            throw new IllegalArgumentException("parameter newPassword can not be blank");
        }
        if(StringUtils.isBlank(req.getOldPassword())){
            throw new IllegalArgumentException("parameter oldPassword can not be blank");
        }
        if(StringUtils.isBlank(req.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be blank");
        }
    }
    
    //修改密码
    @RequestMapping(method = RequestMethod.POST, value = "/updatepassword")
    public BaseResponse<Boolean> updatepassword(@RequestBody CbChangePasswordReq cbChangePasswordReq, HttpServletRequest request){
        BaseResponse<Boolean> response = new BaseResponse<Boolean>();
        try{
            checkParam(cbChangePasswordReq);
            response.setRc(BaseResponse.RC_SUCCESS);
            response.setData(accountBusiness.modifyPassword(cbChangePasswordReq.getUserId(), cbChangePasswordReq.getOldPassword(), cbChangePasswordReq.getNewPassword()));
        }catch(Exception e){
            log.error(e.getMessage());
            response.setRc(BaseResponse.RC_FAIL);
            response.setMsg(e.getMessage());
        }
        return response;
    }
    
    //参数检查
    private void checkParam(UpdateAccountInfo updateAccountInfo){
        if(StringUtils.isBlank(updateAccountInfo.getBusinessType())){
            throw new IllegalArgumentException("parameter businessType can not be blank");
        }
        if(updateAccountInfo.getIdCode() == null){
            throw new IllegalArgumentException("parameter idCode can not be null");
        }
        if(updateAccountInfo.getIdType() == null){
            throw new IllegalArgumentException("parameter idType can not be null");
        }
        if(updateAccountInfo.getName() == null){
            throw new IllegalArgumentException("parameter name can not be null");
        }
        if(StringUtils.isBlank(updateAccountInfo.getUserId())){
            throw new IllegalArgumentException("parameter userId can not be null");
        }
    }
    
    /**
     * 普通用户实名信息修改接口
     * @param updateAccountInfo
     * @return
     */
    @RequestMapping(value = "/updateaccountinfo")
    public BaseResponse<Boolean> updateaccountinfo(@RequestBody UpdateAccountInfo updateAccountInfo){
        try{
            //参数检查
            checkParam(updateAccountInfo);
            
            TAccount tAccount = this.accountBusiness.qryAccountByUserId(updateAccountInfo.getUserId());
            if(tAccount != null){
                TCustomerExample example = new TCustomerExample();
                example.createCriteria().andCustomerIdEqualTo(tAccount.getCustomerId());
                List<TCustomer> tCustomerList = this.tCustomerMapper.selectByExample(example);
                if(CollectionUtils.isNotEmpty(tCustomerList)){
                    TCustomer tCustomer = tCustomerList.get(0);
                    tCustomer.setCustomerName(updateAccountInfo.getName());
                    tCustomer.setIdType(updateAccountInfo.getIdType());
                    tCustomer.setIdCode(updateAccountInfo.getIdCode());
                    tCustomerMapper.updateByPrimaryKey(tCustomer);
                }
                try{
                    callUpdateRealNameInterface(updateAccountInfo);
                }catch(Exception e){
                    log.error("调用清结算(伪)实名信息失败：" + e.getMessage());
                }
            }
            return RspUtils.success(true);
        }catch(Exception e){
            log.info(" updateaccountinfo error :{}",e.getMessage());
            return RspUtils.error("修改实名信息失败:" + e.getLocalizedMessage());
        }
    }
    
    /**
     * 实名信息查询接口
     * @param updateAccountInfo
     * @return
     */
    @RequestMapping(value = "/qryAccountInfo")
    public BaseResponse<UpdateAccountInfo> qryAccountInfo(@RequestBody UpdateAccountInfo updateAccountInfo){
        try{
            TAccount tAccount = this.accountBusiness.qryAccountByUserId(updateAccountInfo.getUserId());
            if(tAccount != null){
                TCustomer tCustomer = tCustomerMapper.selectByPrimaryKey(tAccount.getCustomerId());
                if(tCustomer != null){
                    updateAccountInfo.setIdType(tCustomer.getIdType());
                    updateAccountInfo.setIdCode(tCustomer.getIdCode());
                    updateAccountInfo.setName(tCustomer.getCustomerName());
                }else{
                    throw new IllegalArgumentException("客户信息不存在");
                }
            }else{
                throw new IllegalArgumentException("账户信息不存在");
            }
            return RspUtils.success(updateAccountInfo);
        }catch(Exception e){
            return RspUtils.error(e.getLocalizedMessage());
        }
    }
    
    //调用清结算(伪)实名信息
    private Boolean callUpdateRealNameInterface(UpdateAccountInfo updateAccountInfo){
        Map<String, String> headParams = new HashMap<String, String>();
        String url = dailyTaskConfig.getSettlementCustomerBaseUrl() + "/customer/updateRealNameInfo";
        headParams.put("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessType", updateAccountInfo.getBusinessType());
        params.put("idType", updateAccountInfo.getIdType());
        params.put("idCode", updateAccountInfo.getIdCode());
        params.put("name", updateAccountInfo.getName());
        params.put("userId", updateAccountInfo.getUserId());
      
        String html = "";
        try {
            html = HttpClientUtils.doPost(url, headParams, JSON.toJSONString(params));
            log.info("{} response {}", url, html);
            JSONObject jsonObj = JSON.parseObject(html);
            if(jsonObj.getInteger("rc") != 1){
                throw new IllegalArgumentException(jsonObj.getString("msg"));
            }else{
                return jsonObj.getBoolean("data");
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
}
