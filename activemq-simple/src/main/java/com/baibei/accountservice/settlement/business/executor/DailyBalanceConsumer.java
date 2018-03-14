package com.baibei.accountservice.settlement.business.executor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 日余额对账消费者
 * 从队列取消息写入文件
 * @author peng
 */
@Slf4j
public class DailyBalanceConsumer implements Runnable {

    //文件名
    private String fullFileName;
    
    //日余额对账执行器
    private DailyBalanceExecutor executor; 
    
    public DailyBalanceConsumer (DailyBalanceExecutor executor, String fullFileName){
        this.executor = executor;
        this.fullFileName = fullFileName;
    }
   
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        log.info("DailyBalanceConsumer start");
        File f = new File(fullFileName);
        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter(f, true));
            int pageNo = executor.nextConsumerPage();
            while(pageNo >= 0){
                log.info("DailyBalanceConsumer handle page {}", pageNo);
                try{
                    //从队列里取一条消息
                    StringBuilder sb = executor.getBlockingQueue().take();
                    if(sb.length() > 0){
                        bw.write(sb.toString());
                        bw.flush();
                    }
                    //下一页
                    pageNo = executor.nextConsumerPage();
                }catch(Exception e){
                    log.error(e.getMessage());
                }
            }
        }catch(Exception e){
            log.error(e.getMessage());
        }finally{
            IOUtils.closeQuietly(bw);
        }
        log.info("DailyBalanceConsumer use {} MS", System.currentTimeMillis() - startTime);
        try {
            Thread.sleep(5000);
            log.info("bqueue size " + executor.getBlockingQueue().size());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("DailyBalanceConsumer end");
    }
}
