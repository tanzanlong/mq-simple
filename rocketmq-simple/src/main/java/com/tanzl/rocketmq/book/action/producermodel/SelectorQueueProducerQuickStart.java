package com.tanzl.rocketmq.book.action.producermodel;

import java.io.UnsupportedEncodingException;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.tanzl.rocketmq.book.action.producermodel.selector.OrderMessageQueueSelector;

/**
 * 
 * @author Administrator
 *
 */
public class SelectorQueueProducerQuickStart {
    public static void main (String [] args) throws MQClientException ,
    InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("please_renameunique group name " ) ;
        producer.setInstanceName("instancel" );
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr ("192.168.12.141:9876" ) ;
        producer.start() ;
        for (int i = 0; i < 1000; i++) {
            try {
                Message msg =new Message ("TopicTest " /* Topic */ ," TagA " /* Tag */ ,( "Hello RocketMQ " + i ).getBytes(RemotingHelper.DEFAULT_CHARSET));
                msg.setDelayTimeLevel(3);
                producer.send(msg,new OrderMessageQueueSelector(),new SendCallback(){

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf( " %s %n " ,sendResult);
                        SendStatus sendStatus=sendResult.getSendStatus();
                        System.out.println(sendStatus.name());
                    }
                    
                });
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } /* Message body */ catch (RemotingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            }
        }
        producer.shutdown();
    }
}
