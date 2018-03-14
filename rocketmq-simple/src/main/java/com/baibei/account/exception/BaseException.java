package com.baibei.account.exception;

/**
 * <p></p>
 *
 * @author zhangyue
 * @date 2017/3/20
 */
public class BaseException extends RuntimeException {

    public BaseException(Throwable throwable) {
        super(throwable);
    }

    public BaseException(String msg) {
        super(msg);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }

}
