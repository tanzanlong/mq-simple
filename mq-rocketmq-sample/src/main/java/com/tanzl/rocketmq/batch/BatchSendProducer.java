package com.tanzl.rocketmq.batch;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

public class BatchSendProducer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("broadcastProducer");
        producer.setNamesrvAddr("192.168.212.60:9876");  
        producer.start();

     /*   String topic = "BatchTest";
        MessageBatch batch=new MessageBatch();
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(topic, "TagA", "OrderID001", "Hello world 0".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID002", "Hello world 1".getBytes()));
        messages.add(new Message(topic, "TagA", "OrderID003", "Hello world 2".getBytes()));
        try {
            producer.send(messages);
        } catch (Exception e) {
            e.printStackTrace();
            //handle the error
        }*/
        producer.shutdown();
    }
}
