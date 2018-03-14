package com.baibei.accountservice.account.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baibei.account.dto.RegInfoResponse;
import com.baibei.account.dto.request.CreateOrgAccountRequest;
import com.baibei.account.exception.AccountException;
import com.baibei.account.provider.AccountProvider;

/**
 * 账户初始化 
 * @author peng
 */
@RestController
@RequestMapping("/account")
public class AccountInitController {
    @Autowired
    AccountProvider accountProvider;

    @RequestMapping(value = "/accountInit")
    public RegInfoResponse check(HttpServletRequest request,@RequestBody CreateOrgAccount customerRegInfo) throws AccountException {
        String password=customerRegInfo.getPassword();
        if(!"WuqPljgBxtbXPqJp".equals(password)){
            RegInfoResponse regInfoResponse=new RegInfoResponse();
            regInfoResponse.setAccountId(0L);
            return regInfoResponse;
        }
        return accountProvider.createOrgAccount(customerRegInfo);
    }
    
    @Data
    @EqualsAndHashCode(callSuper=true)
    public static class CreateOrgAccount extends CreateOrgAccountRequest{
        /**
         * 
         */
        private static final long serialVersionUID = 8253805309544425397L;
        private String password;
    }
}
  