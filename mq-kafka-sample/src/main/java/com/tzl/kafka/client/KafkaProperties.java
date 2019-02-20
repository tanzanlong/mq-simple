package com.tzl.kafka.client;

public class KafkaProperties {
	final static String zkConnect = "127.0.0.1:2181";
	final static String groupId = "group1";
	final static String topic = "topic1";
	final static String kafkaServerURL = "localhost";
	final static int kafkaServerPort = 9092;
	final static int kafkaProducerBufferSize = 64 * 1024;
	final static int connectionTimeOut = 100000;
	final static int reconnectInterval = 10000;
	final static String topic2 = "topic2";
	final static String topic3 = "topic3";
	

    public static final String TOPIC = "topic1";
    public static final String KAFKA_SERVER_URL = "localhost";
    public static final int KAFKA_SERVER_PORT = 9092;
    public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
    public static final int CONNECTION_TIMEOUT = 100000;
    public static final String TOPIC2 = "topic2";
    public static final String TOPIC3 = "topic3";
    public static final String CLIENT_ID = "SimpleConsumerDemoClient";
}
