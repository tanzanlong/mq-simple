package com.baibei.accountservice.paycenter.exception;

public class PasswordException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = -6678491912832702945L;
    
    
    

    public PasswordException(Throwable throwable) {
        super(throwable);
    }

    public PasswordException(String msg) {
        super(msg);
    }

}
