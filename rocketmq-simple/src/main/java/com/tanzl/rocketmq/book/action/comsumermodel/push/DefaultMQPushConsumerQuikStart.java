package com.tanzl.rocketmq.book.action.comsumermodel.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

public class DefaultMQPushConsumerQuikStart {
    public static void main(String[] args) throws InterruptedException, MQClientException {
        /**
         * Consumer 的 GroupName 用 于把多个 Consumer 组织到一起 ， 提高并发处理能力， GroupName 需要和消息模式 （ MessageModel ）配合使用 。
         * RocketMQ 支持两种消 息模式 ： Clustering 和 Broadcasting 。
         * 在 Clustering 模式下，同一个 ConsumerGroup ( GroupName 相同 ） 里的每个 Consumer 只消费所订阅消息 的一部分 内 容， 同一个 ConsumerGroup里所有的 Consumer 消 费 的内 容合起来才是所订阅 Topic 内 容 的 整体 ，从而达到负载均衡的目的 。
         * 在 Broadcasting 模式下，同一个 ConsumerGroup 里的每个 Consumer 都能消费到所订阅 Topic 的全部消息，也就是一个消息会被多次分发，被多个 Consumer 消费 。
         * 
         */
       final Map<String,Integer> countMap=new HashMap<String, Integer>();
        DefaultMQPushConsumer Consumer =
                new DefaultMQPushConsumer("RC-MQ-PUSH-C-001");
        Consumer.setNamesrvAddr("192.168.12.141:9876");
        Consumer.setPersistConsumerOffsetInterval(100 *5);
        /**
         * 消费点
         * 若在应用层通过DefaultMQPushConsumer.setOffsetStore(OffsetStore offsetStore)方法设置了DefaultMQPushConsumer.offsetStore变量，则将offsetStore变量赋值给DefaultMQPushConsumerImpl.offsetStore变量；
         * 若没有设置则根据消息模式来设置：
         * 若消息模式是广播（BROADCASTING），则初始化LocalFileOffsetStore对象并赋值给DefaultMQPushConsumerImpl.offsetStore变量；
         * 若消息模式是集群（CLUSTERING），则初始化RemoteBrokerOffsetStore对象并赋值给DefaultMQPushConsumerImpl.offsetStore变量；
         */
        Consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        Consumer.setMessageModel(MessageModel.CLUSTERING);
        Consumer.subscribe("RC-MQ-PUSH-C-001-T001", "*");
        //Consumer.setOffsetStore(offsetStore);
        Consumer.registerMessageListener(new MessageListenerConcurrently() {
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                    ConsumeConcurrentlyContext context) {
                final MessageExt[] messageExts = new MessageExt[1];
                Integer count=countMap.get("tcount");
                if(count==null||count==0){
                    count=1;
                }else{
                    count=count+1; 
                }
                messageExts[0] = msgs.get(0);
                if(System.currentTimeMillis()-messageExts[0].getBornTimestamp()>60*1000) {//一分钟之前的认为过期
                  return  ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//过期消息跳过
                }

                System.out.println(new String(messageExts[0].getBody()));
               /* System.out.printf(Thread.currentThread().getName() + " Receive "+count+"New Messages: "
                        + msgs + "%n");*/
                countMap.put("tcount", count);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        Consumer.start();
    }
}
