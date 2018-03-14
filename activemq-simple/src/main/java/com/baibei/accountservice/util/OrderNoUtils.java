package com.baibei.accountservice.util;

import java.util.UUID;

public class OrderNoUtils {

    /**
     * 生成第三方流水号
     * @param businessCode
     * @return
     */
    public static String genOrderNo() {
    	return UUID.randomUUID().toString().replace("-", "");
    }
}
