package com.baibei.accountservice.paycenter.exception;

public class PayException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = -6678491912832702945L;

    public PayException(Throwable throwable) {
        super(throwable);
    }

    public PayException(String msg) {
        super(msg);
    }

}
