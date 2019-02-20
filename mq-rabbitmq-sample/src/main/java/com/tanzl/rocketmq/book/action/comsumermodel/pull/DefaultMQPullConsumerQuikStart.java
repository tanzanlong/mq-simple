package com.tanzl.rocketmq.book.action.comsumermodel.pull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageQueue;

public class DefaultMQPullConsumerQuikStart {

    private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();

    public static void main(String[] args) throws MQClientException {
        DefaultMQPullConsumer consumer =
                new DefaultMQPullConsumer("RC-MQ-PULL-C-001");
        consumer.setNamesrvAddr("192.168.12.141:9876");
        consumer.setConsumerGroup("RC-MQ-PULL-C-001");
        consumer.start();
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("RC-MQ-PULL-C-001-T001");
        System.out.println(mqs.size());
        for (MessageQueue mq : mqs) {
            long Offset = consumer.fetchConsumeOffset(mq, true);
            System.out.println("");
            SINGLE_MQ: 
                while (true) {
                try {
                    PullResult pullResult =
                            consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                    System.out.printf(""+pullResult.getMsgFoundList());
                    putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        consumer.shutdown();
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long Offset = OFFSE_TABLE.get(mq);
        if (Offset != null)
            return Offset;
        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long Offset) {
        OFFSE_TABLE.put(mq, Offset);
    }
}
