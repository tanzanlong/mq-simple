package com.baibei.accountservice.util;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

import com.mysql.jdbc.log.LogUtils;

@Component
public class LockUtils {

    static final Logger logger = LoggerFactory.getLogger(LogUtils.class);
    
    @Autowired
    JedisConnectionFactory jedisConnectionFactory;
    
    public Boolean getLock(String lockName, int seconds){
        RedisClusterConnection conn = null;
        Boolean result = true;
        try{
            conn = jedisConnectionFactory.getClusterConnection();
            byte[] key = lockName.getBytes();
            byte[] value = (System.currentTimeMillis() + "").getBytes();
            result = conn.setNX(key, value);
            if(result == true){
                logger.info("get lock {} success", lockName);
                conn.expire(key, seconds);
            }else{//由于setNX和expire不是同一个原子操作，存在setNX成功，expire失败的情况，所以需要强制delete机制
                logger.info("get lock {} failure", lockName);
                value = conn.get(key);
                if(value != null){
                    if(System.currentTimeMillis() - NumberUtils.toLong(new String(value)) > 2 * 1000 * seconds){
                        logger.warn("warn: release lock {} {}", lockName, NumberUtils.toLong(new String(value)));
                        conn.del(key);
                    }
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage());
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(Exception unused){
                }
            }
        }
        return result;
    }
}
