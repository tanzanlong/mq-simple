package com.baibei.accountservice.util;

/**
 * <p>
 *     主键生成器
 * </p>
 *
 * @author zhangyue
 * @date 2017/4/24
 */
public class IDGenerator {

    private static SnowflakeIdWorker snowflakeIdWorker;

    static {
        //先动态获取，分库分表后
        snowflakeIdWorker = new SnowflakeIdWorker(1,1);
    }

    /**
     * 生成ID
     * @return
     */
    public static Long next() {
        return snowflakeIdWorker.nextId();
    }


}
