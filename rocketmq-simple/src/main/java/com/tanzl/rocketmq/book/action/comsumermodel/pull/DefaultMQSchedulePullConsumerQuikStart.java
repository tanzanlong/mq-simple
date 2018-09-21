package com.tanzl.rocketmq.book.action.comsumermodel.pull;

import java.util.Date;

import com.alibaba.rocketmq.client.consumer.MQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumerScheduleService;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.PullTaskCallback;
import com.alibaba.rocketmq.client.consumer.PullTaskContext;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

public class DefaultMQSchedulePullConsumerQuikStart {

    public static void main(String[] args) throws MQClientException {
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("RC-MQ-PULL-C-001");
        scheduleService.getDefaultMQPullConsumer().setNamesrvAddr("192.168.12.141:9876");
        scheduleService.setMessageModel(MessageModel.CLUSTERING);
        scheduleService.registerPullTaskCallback("RC-MQ-PULL-C-001-T001", new PullTaskCallback() {
 
            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {
 
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0)
                        offset = 0;
 
                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    System.out.println(new Date()+"--"+offset + "\t" + mq + "\t" + pullResult);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
 
                    //设置隔多长时间进行下次拉去
                    context.setPullNextDelayTimeMillis(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
 
        scheduleService.start();
    }

}
