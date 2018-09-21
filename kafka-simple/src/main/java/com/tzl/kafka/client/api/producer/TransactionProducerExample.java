package com.tzl.kafka.client.api.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class TransactionProducerExample {
    /**

     * 需要:

     * 1、设置transactional.id

     * 2、设置enable.idempotence

     * @return

     */

    private Producer<String, String> buildProducer() {
        // create instance for properties to access producer configs
        Properties props = new Properties();
        // bootstrap.servers是Kafka集群的IP地址。多个时,使用逗号隔开
        props.put("bootstrap.servers", "localhost:9092");
        // 设置事务id
        props.put("transactional.id", "first-transactional");
        // 设置幂等性
        props.put("enable.idempotence",true);
        //Set acknowledgements for producer requests.
        props.put("acks", "all");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 1);
        //Specify buffer size in config,这里不进行设置这个属性,如果设置了,还需要执行producer.flush()来把缓存中消息发送出去
        //props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        // Kafka消息是以键值对的形式发送,需要设置key和value类型序列化器
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",

                "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }
    
    /**

     * 在一个事务只有生产消息操作

     */

    public void onlyProduceInTransaction() {
        Producer<String, String> producer = buildProducer();
        // 1.初始化事务
        producer.initTransactions();
        // 2.开启事务
        producer.beginTransaction();
        try {
            // 3.kafka写操作集合
            // 3.1 do业务逻辑
            // 3.2 发送消息
            producer.send(new ProducerRecord<String, String>("test", "transaction-data-1"));
            producer.send(new ProducerRecord<String, String>("test", "transaction-data-2"));
            // 3.3 do其他业务逻辑,还可以发送其他topic的消息。
            // 4.事务提交
            producer.commitTransaction();
        } catch (Exception e) {
            // 5.放弃事务
            producer.abortTransaction();

        }
    }
}
