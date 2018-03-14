package com.baibei.account.dto.exception;

/**
 * 余额不足异常
 * @author peng
 *
 */
public class BalanceNotEnoughException extends RuntimeException {

    private static final long serialVersionUID = -3085186608675944621L;

    public BalanceNotEnoughException(String message){
        super(message);
    }
}
