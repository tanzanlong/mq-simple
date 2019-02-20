package com.tanzl.rocketmq.book.action.comsumermodel.pull;

import java.util.Set;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MessageQueueListener;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageQueue;

public class DefaultMQPullConsumer2QuikStart {


    public static void main(String[] args) throws MQClientException {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer();
        consumer.setNamesrvAddr("192.168.12.141:9876");
        consumer.setConsumerGroup("RC-MQ-PULL-C-001");
        try {
            consumer.start();
            Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues("RC-MQ-PULL-C-001-T001");

            for (MessageQueue messageQueue : messageQueues) {

                System.out.println(messageQueue.getTopic()+":"+messageQueue);
            }


            // 消息队列的监听
            consumer.registerMessageQueueListener("RC-MQ-PULL-C-001-T001", new MessageQueueListener() {

                @Override
                // 消息队列有改变，就会触发
                public void messageQueueChanged(String topic, Set<MessageQueue> mqAll,
                        Set<MessageQueue> mqDivided) {
                    // TODO Auto-generated method stub

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
