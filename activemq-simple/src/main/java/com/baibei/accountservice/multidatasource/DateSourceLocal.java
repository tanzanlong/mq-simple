package com.baibei.accountservice.multidatasource;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by l_yy on 2017/10/12.
 */
@Slf4j
public class DateSourceLocal {

    private static ThreadLocal local = new ThreadLocal();


    private static Map<String, String> exchange2DateSourceMap;


    static{

        Properties p = new Properties();
        try {
            p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/exchange2DateSource.properties"));
            exchange2DateSourceMap = new HashMap<String, String>((Map)p);
            log.info("init exchange2DateSourceMap success!");
            log.info("exchange2DateSourceMap->{}",exchange2DateSourceMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> reportMap4Iterator() {
        return Collections.unmodifiableMap(exchange2DateSourceMap);
    }

    public static void setExchangeTag(String exchangeTag) {
        local.set(exchangeTag);
    }

    public static String getExchangeTag() {
        return (String)local.get();
    }


    public static String getDateSource() {
        return exchange2DateSourceMap.get((String)local.get());
    }

    public static void clean() {
        local.remove();
    }



    public static void main(String[] args) {
        Map<String, String> exchange2DateSourceMap = DateSourceLocal.reportMap4Iterator();
        for (Map.Entry<String, String> entry : exchange2DateSourceMap.entrySet()) {
            String exchangeTag = entry.getKey();
            DateSourceLocal.setExchangeTag(exchangeTag);
            //


        }
    }

}
