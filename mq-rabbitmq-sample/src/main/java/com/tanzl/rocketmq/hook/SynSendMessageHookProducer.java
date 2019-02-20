package com.tanzl.rocketmq.hook;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.SendMessageContext;
import com.alibaba.rocketmq.client.hook.SendMessageHook;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

/**
 * 同步发送
 * 
 * @author tony
 *
 */
@Slf4j
public class SynSendMessageHookProducer {
    
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("PT-HOOK");
        
        producer.getDefaultMQProducerImpl().registerSendMessageHook(new SendMessageHook() {
            @Override
            public String hookName() {
                return "TestHook";
            }
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            @Override
            public void sendMessageBefore(final SendMessageContext context) {
                /*assertionErrors[0] = assertInOtherThread(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(context.getMessage()).isEqualTo(message);
                        assertThat(context.getProducer()).isEqualTo(producer);
                        assertThat(context.getCommunicationMode()).isEqualTo(CommunicationMode.SYNC);
                        assertThat(context.getSendResult()).isNull();
                    }
                });*/
                log.info( new String(context.getMessage().getBody()));
                countDownLatch.countDown();
            }

            @Override
            public void sendMessageAfter(final SendMessageContext context) {
                log.info( new String(context.getMessage().getBody()));
              /*  assertionErrors[0] = assertInOtherThread(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(context.getMessage()).isEqualTo(message);
                        assertThat(context.getProducer()).isEqualTo(producer.getDefaultMQProducerImpl());
                        assertThat(context.getCommunicationMode()).isEqualTo(CommunicationMode.SYNC);
                        assertThat(context.getSendResult()).isNotNull();
                    }
                });*/
                countDownLatch.countDown();
            }
        });
        
        producer.setNamesrvAddr("192.168.12.141:9876");
        producer.start();
        try {
            for (int i = 0; i < 6000000; i++) {
                Message msg =
                        new Message("TopicTPHOOK", "tarHOOK", "OrderID001",
                                "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));

                msg.putUserProperty("SequenceId", String.valueOf(i));
                SendResult sendResult = producer.send(msg);
                System.out.printf("%s%n", sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();

    }


}
