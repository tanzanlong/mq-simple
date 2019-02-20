package com.tzl.kafka.client.api.consumer.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**在某些情况下，您可能希望通过明确指定偏移量来更好地控制已提交的记录。在下面的示例中，我们在完成处理每个分区中的记录后提交偏移量。
 * @author Administrator
 *
 */
public class PartitionManualAssignConsumer {
    private final static boolean running = true;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers",
                "192.168.12.141:9091,192.168.12.141:9092,192.168.12.141:9093");
        props.put("group.id", "MC-T-0001");
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
      //  consumer.subscribe(Arrays.asList("test"));
        final int minBatchSize = 200;
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        try {
            String topic = "test";
            TopicPartition partition0 = new TopicPartition(topic, 0);
            TopicPartition partition1 = new TopicPartition(topic, 1);
            consumer.assign(Arrays.asList(partition0, partition1));
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<String, String>> partitionRecords =
                            records.records(partition);
                    for (ConsumerRecord<String, String> record : partitionRecords) {
                        System.out.println(record.offset() + ": " + record.value());
                    }
                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(
                            lastOffset + 1)));
                }
            }
        } finally {
            consumer.close();
        }


    }

}
