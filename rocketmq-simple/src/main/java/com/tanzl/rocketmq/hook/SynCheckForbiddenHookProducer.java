package com.tanzl.rocketmq.hook;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.hook.CheckForbiddenContext;
import com.alibaba.rocketmq.client.hook.CheckForbiddenHook;
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
public class SynCheckForbiddenHookProducer {
    
    public static void main(String[] args) throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("PT-HOOK");
        
        producer.getDefaultMQProducerImpl().registerCheckForbiddenHook(new CheckForbiddenHook(){
           public String hookName(){
               return "TestCheckForbiddenHook";
            }

           public  void checkForbidden(final CheckForbiddenContext context) throws MQClientException{
               log.info(new String(context.getMessage().getBody()));
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
