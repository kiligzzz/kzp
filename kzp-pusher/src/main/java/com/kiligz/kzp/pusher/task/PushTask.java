package com.kiligz.kzp.pusher.task;

import cn.hutool.extra.spring.SpringUtil;
import com.kiligz.kzp.common.domain.Status;
import com.kiligz.kzp.common.exception.KzpException;
import com.kiligz.kzp.pusher.domain.AbstractMessage;
import com.kiligz.kzp.pusher.channel.email.EmailChannel;

import java.util.concurrent.TransferQueue;

/**
 * @author Ivan
 * @since 2022/12/28
 */
public class PushTask implements Runnable {
    private final TransferQueue<AbstractMessage> messageQueue =
            ConcurrentManager.getTransferQueue("messageQueue", AbstractMessage.class);

    @Override
    public void run() {
        while (true) {
            try {
                AbstractMessage message = messageQueue.take();
                if (ConcurrentManager.isEndMarker(messageQueue, new AbstractMessage(){}))
                    break;

                SpringUtil.getBean(EmailChannel.class).send(message);
            } catch (Exception e) {
                throw new KzpException(Status.fail());
            }
        }
    }

}
