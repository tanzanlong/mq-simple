package com.tanzl.rocketmq.book.action.comsumermodel;

import java.io.UnsupportedEncodingException;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

public class ClusterPushProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException, UnsupportedEncodingException {

        DefaultMQProducer producer = new DefaultMQProducer("RC-MQ-PUSH-P-001");
        producer.setNamesrvAddr("192.168.12.141:9876");
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        for (int i = 0; i < 100; i++) {
            try {
                final int index = i;
                Message msg =
                        new Message("RC-MQ-PUSH-C-001-T001",
                    "TagA",
                    "OrderID"+i,
                    ("Hello world0917:"+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg, new SendCallback() {
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    public void onException(Throwable e) {
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
        producer.shutdown();
    }
}
