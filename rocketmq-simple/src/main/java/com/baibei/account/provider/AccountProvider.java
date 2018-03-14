package com.baibei.account.provider;

import com.baibei.account.dto.RegInfoResponse;
import com.baibei.account.dto.request.ConfirmPasswordRequest;
import com.baibei.account.dto.request.CreateOrgAccountRequest;
import com.baibei.account.dto.request.CreatePersonalAccountRequest;
import com.baibei.account.dto.request.VerifyCreateAccountRequest;
import com.baibei.account.exception.AccountException;

/**
 * 提供账户相关接口,包括开户\销户\修改密码\重置密码等接口
 * Created by keegan on 11/05/2017.
 */
public interface AccountProvider {

    /**
     * 个人用户开户
     * @param request 请求参数
     * @return 资金账户ID
     */
    RegInfoResponse createPersonalAccount(CreatePersonalAccountRequest request) throws AccountException;

    /**
     * 机构用户开户
     * @param request 请求参数
     * @return 资金账户ID
     */
    RegInfoResponse createOrgAccount(CreateOrgAccountRequest request)  throws AccountException;

    /**
     * 销户
     * @param userId 用户ID
     */
    Boolean deleteAccount(String userId)  throws AccountException;

    /**
     * 修改资金密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    Boolean modifyPassword(String userId, String oldPassword, String newPassword)  throws AccountException;

    /**
     * 重置资金密码
     * @param userId 用户ID
     * @param password 新密码
     */
    Boolean resetPassword(String userId, String password)  throws AccountException;

    /**
     * 重置资金密码(短信下发)
     * @param userId 用户ID
     */
    Boolean resetPasswordBySms(String userId,String moblie)  throws AccountException;
    
    /**
     * 验证开户信息是否能正常开户
     * 
     * @return
     */
    Boolean verifyCreateAccountMsg(VerifyCreateAccountRequest verifyCreateAccountRequest);


    /**
     * 校验资金密码
     * 
     * @param confirmPasswordRequest
     * @return
     */
    Boolean confirPassWord(ConfirmPasswordRequest confirmPasswordRequest);
}
