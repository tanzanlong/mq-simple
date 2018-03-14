package com.baibei.accountservice.controller.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class PressTestOrderCreator {
 public static void main(String[] args) throws IOException {
     String filepathName="F:\\sixorder30w.csv";
     File file = new File(filepathName);
    
     OutputStreamWriter ow = null;
     ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
   //构建输出流，同时指定编码
     try {
          
          for (Long i = 200000000L; i < 200050001; i++) { 
            
              for (int j = 0; j < 6; j++) {
                 // String content="{\"businessType\":\"WINE\",\"orderId\":\" "+i+""+j+" \",\"orderType\":\"1\",\"orderTime\": 1492484049000,\"productCode\":\"GDAG\",\"items\":[{\"userId\":\""+i+"\",\"accountId\":"+i+" ,\"amount\":10000,\"feeType\":1}";
                 String content="{\"businessType\":\"WP\",\"orderId\":\"S30w"+i+""+j+"\",\"orderType\":2001,\"orderTime\": "+new Date().getTime()+",\"items\":[{\"amount\":-20800,\"accountId\":"+i+",\"userId\":\""+i+"\",\"feeType\":20012},{\"amount\":20320,\"accountId\":"+i+",\"userId\":\""+i+"\",\"feeType\":20013},{\"amount\":-2400,\"accountId\":"+i+",\"userId\":\""+i+"\",\"feeType\":20011},{\"amount\":20800,\"accountId\":"+(i+1000000)+",\"userId\":\""+(i+1000000)+"\",\"feeType\":20012},{\"amount\":-20320,\"accountId\":"+(i+1000000)+",\"userId\":\""+(i+1000000)+"\",\"feeType\":20013},{\"amount\":1920,\"accountId\":"+(i+1000000)+",\"userId\":\""+(i+1000000)+"\",\"feeType\":20011},{\"amount\":240,\"accountId\":"+100000000+",\"userId\":\""+100000000+"\",\"feeType\":20011},{\"amount\":240,\"accountId\":"+100000001+",\"userId\":\""+100000001+"\",\"feeType\":20011}]}";
               
                 //String content="{\"businessType\":\"WP\",\"orderId\":\"3002298075417091515070952\",\"orderType\":2001,\"orderTime\": 1505459468000,\"items\":[{\"amount\":-20800,\"accountId\":357559701806473216,\"userId\":\"2980754_002\",\"feeType\":20012},{\"amount\":20320,\"accountId\":357559701806473216,\"userId\":\"2980754_002\",\"feeType\":20013},{\"amount\":-2400,\"accountId\":357559701806473216,\"userId\":\"2980754_002\",\"feeType\":20011},{\"amount\":20800,\"accountId\":357475531166371840,\"userId\":\"18023\",\"feeType\":20012},{\"amount\":-20320,\"accountId\":357475531166371840,\"userId\":\"18023\",\"feeType\":20013},{\"amount\":1920,\"accountId\":357475531166371840,\"userId\":\"18023\",\"feeType\":20011},{\"amount\":240,\"accountId\":357475546823708672,\"userId\":\"-2\",\"feeType\":20011},{\"amount\":240,\"accountId\":357475546404278272,\"userId\":\"-1\",\"feeType\":20011}]}";
                 
                  
                  //String content="{\"businessType\":\"WP\",\"orderId\":\""+i+""+j+"\",\"orderType\":\"2004\",\"orderTime\":"+new Date().getTime()+",\"items\":[{\"userId\":\""+i+"\",\"accountId\":"+i+",\"amount\":-1000,\"feeType\":20041},{\"userId\":\""+i+"\",\"accountId\":"+i+",\"amount\":-10,\"feeType\":20042}]}";
                  System.out.println(content);
                  ow.write(content);
                 // ow.write("\r\n");
                  //写完一行换行
                  ow.write("\r\n");
                  ow.flush();
            }
              
        }
         
          
     } catch (UnsupportedEncodingException | FileNotFoundException e1) {
         System.out.println(e1);
     }finally{
         ow.close();
     }
}
}
