package com.baibei.account.exception;

/**
 *
 *
 */
public class ProductCodeNotExsitException extends RuntimeException {

    public ProductCodeNotExsitException(Long userId) {
        super("user doest not exist or cancelled: userId = " + userId);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}