package com.tanzl.rocketmq.book.action.producermodel.selector;

import java.util.List;

import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;

public class OrderMessageQueueSelector implements MessageQueueSelector {
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object orderKey) {
        int id = Integer.parseInt(orderKey.toString());
        int idMainindex = id / 100;
        int size = mqs.size();
        int index = idMainindex % size;
        return mqs.get(index);
    }
}
