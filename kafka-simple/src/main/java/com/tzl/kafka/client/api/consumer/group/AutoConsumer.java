package com.tzl.kafka.client.api.consumer.group;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class AutoConsumer {
public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.12.141:9091,192.168.12.141:9092,192.168.12.141:9093");
    props.put("group.id", "G-T-001");
    props.put("consumer.id", "C1");
    props.put("client.id", "CC1");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Arrays.asList("test"));
    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records)
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
    }
   // consumer.close();
}
}
