package com.baibei.accountservice.paycenter.vo.response;

import java.io.UnsupportedEncodingException;

import com.baibei.accountservice.paycenter.utill.MD5;

import lombok.Getter;
import lombok.Setter;


/**
 * 入金异步通知
 * @author peng
 *
 */
public class WithdrawNotify {
    
    @Setter
    @Getter
    private String orderId;
    
    @Setter
    @Getter
    private String orderStatus;
    
    @Setter
    @Getter
    private Long amount;
    
    @Setter
    @Getter
    private String sign;
    
    public boolean checkSign(String appSecret){
        StringBuilder sb = new StringBuilder();
        sb.append("amount=");
        sb.append(amount);
        sb.append("orderId=");
        sb.append(orderId);
        sb.append("orderStatus=");
        sb.append(orderStatus);
        sb.append(appSecret);
        try {
            String str = MD5.getHashString(sb.toString(), "UTF-8");
            if(str.equalsIgnoreCase(sign)){
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

}
