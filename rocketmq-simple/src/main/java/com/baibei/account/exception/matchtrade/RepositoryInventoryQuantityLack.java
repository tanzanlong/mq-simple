package com.baibei.account.exception.matchtrade;

/**
 * 库存数量不足异常
 * Created by zorro on 2017/5/10.
 */
public class RepositoryInventoryQuantityLack extends RuntimeException{
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
