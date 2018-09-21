package com.tzl.kafka.client.api.producer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


public class KafkaSendWayProducerExample {
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
     * 同步发送
     */
    public void syncSend(){
        Producer<String, String> producer = buildProducer();
        ProducerRecord<String, String> producerRecord=new ProducerRecord<>("TT-20180921-001",
                "",
                "HELLO");
        Future<RecordMetadata> future=producer.send(producerRecord);
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 异步发送
     */
    public void asyncSend(){
        Producer<String, String> producer = buildProducer();
        ProducerRecord<String, String> producerRecord=new ProducerRecord<>("TT-20180921-001",
                "",
                "HELLO");
        Future<RecordMetadata> future=producer.send(producerRecord, new DemoCallBack1());
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}




class DemoCallBack1 implements Callback {
    
    public DemoCallBack1(){super();}

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (metadata != null) {
            System.out.println(
                "message( sent to partition(" + metadata.partition() +
                    "), " +
                    "offset(" + metadata.offset() + ")");
        } else {
            exception.printStackTrace();
        }
    }
}
