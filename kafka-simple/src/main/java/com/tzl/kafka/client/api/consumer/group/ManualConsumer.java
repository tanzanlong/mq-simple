package com.tzl.kafka.client.api.consumer.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**用户还可以控制何时应将记录视为已消耗并因此提交其偏移量，而不是依赖于消费者定期提交消耗的偏移量。当消息的消耗与某些处理逻辑耦合时，这是有用的，因此消息在完成处理之前不应被视为已消耗。
 * @author Administrator
 *
 */
public class ManualConsumer {
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
