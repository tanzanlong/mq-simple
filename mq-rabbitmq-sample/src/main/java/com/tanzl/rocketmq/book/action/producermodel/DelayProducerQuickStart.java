package com.tanzl.rocketmq.book.action.producermodel;

import java.io.UnsupportedEncodingException;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

/**目前延迟的时间不支
持任意设置，仅支持预设值的时间长度 （ 1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/
7m/8m/9m/10m/20m/30m/1h/2h ） 。 比如 setDelayTimeLevel(3) 表示延迟 10s 
 * @author Administrator
 *
 */
public class DelayProducerQuickStart {
    public static void main (String [] args) throws MQClientException ,
    InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("please_renameunique group name " ) ;
        producer.setInstanceName("instancel" );
        producer.setRetryTimesWhenSendFailed(3);
        producer.setNamesrvAddr ("192.168.12.141:9876" ) ;
        producer.start() ;
        for (int i = 0; i < 1000; i++) {
            try {
                Message msg =new Message ("TopicTest " /* Top 工 c */ ," TagA " /* Tag */ ,( "Hello RocketMQ " + i ).getBytes(RemotingHelper.DEFAULT_CHARSET));
                msg.setDelayTimeLevel(3);
                producer.send(msg,new SendCallback(){

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
            }
        }
        producer.shutdown();
    }
}
