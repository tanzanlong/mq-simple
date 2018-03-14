package com.baibei.accountservice.settlement.business.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baibei.accountservice.settlement.business.SettlementBusiness;

import lombok.extern.slf4j.Slf4j;

/**
 * 多生产者，单消费者处理模型
 * @author peng
 *
 */
@Component
@Slf4j
public class DailyBalanceExecutor {

    @Autowired
    private SettlementBusiness settlementBusiness;
    
    //生产者线程总数
    private int producerCount;
    
    //总页数
    private int totalPage;
    
    //每页记录数
    private int pageSize;
    
    //当前生产者处理的页数
    private int currentProducerPageNo = 0;
    
    //当前消费者处理的页数
    private int currentConsumerPage = 0;
    
    //队列
    private BlockingQueue<StringBuilder> blockingQueue = null;
    
    //全路径文件名
    private String fullFileName;
    
    //业务类型
    private String businessType;
    
    public int getTotalPage(){
        return totalPage;
    }
    
    //生产者翻页，如果为-1，表示翻到最后一页（无数据页）了
    public synchronized int nextProducerPage(){
        currentProducerPageNo++;
        if(currentProducerPageNo <= totalPage){
            return currentProducerPageNo;
        }else{
            return -1;
        }
    }
    
    //消费者翻页，如果为-1，表示翻到最后一页（无数据页）了
    public synchronized int nextConsumerPage(){
        currentConsumerPage++;
        if(currentConsumerPage <= totalPage){
            return currentConsumerPage;
        }else{
            return -1;
        }
    }
    
    public int getPageSize(){
        return this.pageSize;
    }
    
    public SettlementBusiness getSettlementBusiness(){
        return this.settlementBusiness;
    }
    
    public BlockingQueue<StringBuilder> getBlockingQueue(){
        return this.blockingQueue;
    }
    
    //初始化
    public void init(int producerCount, int totalPage, String fullFileName, String businessType, int pageSize){
        this.producerCount = producerCount;
        this.totalPage = totalPage;
        this.fullFileName = fullFileName;
        this.businessType = businessType;
        this.pageSize = pageSize;
    }
    
    //启动执行器
    public void execute(){
        log.info("DailyBalanceExecutor start");
        //初始化队列，大小为总页数
        blockingQueue = new ArrayBlockingQueue<StringBuilder>(totalPage);
        //启动多个生产者
        ExecutorService executorService = Executors.newFixedThreadPool(producerCount + 1);
        for(int i=0; i<producerCount; i++){
            executorService.execute(new DailyBalanceProducer(this, i+1, businessType));
        }
        //启动单个消费者
        executorService.execute(new DailyBalanceConsumer(this, fullFileName));
        executorService.shutdown();
        log.info("DailyBalanceExecutor end");
    }
}
