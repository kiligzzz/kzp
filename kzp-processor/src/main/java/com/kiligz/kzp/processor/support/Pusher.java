package com.kiligz.kzp.processor.support;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * @author Ivan
 * @since 2023/1/11
 */
@RequiredArgsConstructor
public class Pusher {

//    private final RocketMQTemplate rocketMQTemplate;

    @StreamListener
    public void send() {
        /*异步消息需要设置回调对象，消息发送成功/失败后，会由另外一个线程调用对象中的方法*/
//        rocketMQTemplate.asyncSend("s", "s", new SendCallback() {
//            @Override
//            public void onSuccess(SendResult sendResult) {
//            }
//            @Override
//            public void onException(Throwable throwable) {
//            }
//        },100000);
//        source.output().send(MessageBuilder.withPayload("sss").build());
    }
}
