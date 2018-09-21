package com.tanzl.rocketmq.ordermsg;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

public class ConsumerInOrder {
    public static void main(String[] args) throws MQClientException {  
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name_3");
        DefaultMQPullConsumer pullconsumer=new DefaultMQPullConsumer();
        consumer.setNamesrvAddr("192.168.212.60:9876");  
        /** 
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br> 
         * 如果非第一次启动，那么按照上次消费的位置继续消费 
         */  
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);  
   
        consumer.subscribe("TopicTestOrder", "TagA || TagC || TagD");  
   
        consumer.registerMessageListener(new MessageListenerOrderly() {  
   
            Random random = new Random();  
   
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {  
                context.setAutoCommit(true);  
                System.out.print(Thread.currentThread().getName() + " Receive New Messages: " );  
                for (MessageExt msg: msgs) {  
                    System.out.println(msg + ", content:" + new String(msg.getBody()));  
                }  
                try {  
                    //模拟业务逻辑处理中...  
                    TimeUnit.SECONDS.sleep(random.nextInt(10));  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                return ConsumeOrderlyStatus.SUCCESS;  
            }  
        });  
   
        consumer.start();  
   
        System.out.println("Consumer Started.");  
    }  
}
