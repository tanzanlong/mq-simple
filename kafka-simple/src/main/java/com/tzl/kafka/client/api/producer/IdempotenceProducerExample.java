package com.tzl.kafka.client.api.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class IdempotenceProducerExample {
    private Producer<String, String> buildIdempotProducer() {
        // create instance for properties to access producer configs
        Properties props = new Properties();
        // bootstrap.servers是Kafka集群的IP地址。多个时,使用逗号隔开
        props.put("bootstrap.servers", "192.168.12.141:9091,192.168.12.141:9092,192.168.12.141:9093");
        props.put("enable.idempotence", true);
        // If the request fails, the producer can automatically retry,
        props.put("retries", 3);
        // Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        // The buffer.memory controls the total amount of memory available to the producer for
        // buffering.

        props.put("buffer.memory", 33554432);
        // Kafka消息是以键值对的形式发送,需要设置key和value类型序列化器
        props.put("key.serializer",
        "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
        "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;

    }
    
    public void produceIdempotMessage(String topic, String message) {
        // 创建Producer
        Producer<String, String> producer = buildIdempotProducer();
        // 发送消息
        producer.send(new ProducerRecord<String, String>(topic, message));
        producer.flush();

    }
    
    public static void main(String[] args) {
        IdempotenceProducerExample tdempotenceProducerExample=new IdempotenceProducerExample();
        tdempotenceProducerExample.produceIdempotMessage("test", "hello");
    }
}
