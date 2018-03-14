package com.baibei.account.exception;

public class AccountException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = -6678491912832702945L;


    public AccountException(Throwable throwable) {
        super(throwable);
    }

    public AccountException(String msg) {
        super(msg);
    }
}
