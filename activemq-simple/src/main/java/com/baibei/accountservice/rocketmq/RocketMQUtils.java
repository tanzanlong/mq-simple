package com.baibei.accountservice.rocketmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.baibei.accountservice.config.DynamicConfig;

@Component
public class RocketMQUtils {

    @Autowired
    DynamicConfig dynamicConfig;
    
    private DefaultMQProducer producer = null;
    
    public void init() throws Exception{
        producer = new DefaultMQProducer("DefaultMQProducer");  
        producer.setNamesrvAddr(dynamicConfig.getRocketMqNameAddr());
        producer.start();
    }
    
    public boolean send(String topic, String message) throws Exception{
        Message msg = new Message(topic, null,  null, message.getBytes("UTF-8"));
        SendResult sendResult = producer.send(msg);
        if(sendResult.getSendStatus() == SendStatus.SEND_OK){
            return true;
        }
        return false;
    }
    
    public boolean send(String topic, String tags, String keys, String message) throws Exception{
        Message msg = new Message(topic, tags,  keys, message.getBytes("UTF-8"));
        SendResult sendResult = producer.send(msg);
        if(sendResult.getSendStatus() == SendStatus.SEND_OK){
            return true;
        }
        return false;
    }
    
    public void shutdown(){
        if(producer != null){
            producer.shutdown();
        }
    }
    
    public static void main(String[] args) throws Exception{
        RocketMQUtils rocketMQUtils = new RocketMQUtils();
        rocketMQUtils.init();
        rocketMQUtils.send("PayCenter", "测试中文message1");
        rocketMQUtils.shutdown();
    }
}
