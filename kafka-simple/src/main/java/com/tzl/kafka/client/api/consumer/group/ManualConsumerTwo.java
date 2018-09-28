package com.tzl.kafka.client.api.consumer.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ManualConsumerTwo {
public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.12.141:9091,192.168.12.141:9092,192.168.12.141:9093");
    props.put("group.id", "MC-T-0001");
    props.put("enable.auto.commit", "false");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Arrays.asList("test"));
    final int minBatchSize = 200;
    List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
            buffer.add(record);
        }
        if (buffer.size() >= minBatchSize) {
            //insertIntoDb(buffer);
            consumer.commitSync();
            buffer.clear();
        }
    }
    

}
}
