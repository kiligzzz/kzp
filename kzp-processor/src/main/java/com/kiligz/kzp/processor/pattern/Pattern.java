package com.kiligz.kzp.processor.pattern;

import com.kiligz.kzp.common.constant.Consts;
import com.kiligz.kzp.common.domain.MsgWrapper;
import com.kiligz.kzp.common.domain.PushWrapper;
import com.kiligz.kzp.common.utils.SpringUtil;
import com.kiligz.kzp.processor.support.Pusher;
import lombok.Getter;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息触发模式抽象类
 *
 * @author Ivan
 * @since 2022/12/30
 */
@Getter
public abstract class Pattern {
    /**
     * 消息队列，缓冲消费
     */
    private final BlockingQueue<MsgWrapper> msgQueue = new LinkedBlockingQueue<>(Consts.DEFAULT_CAPACITY);

    /**
     * 推送工具类
     */
    protected final Pusher pusher = SpringUtil.getBean(Pusher.class);

    /**
     * 不停消费队列
     */
    @PostConstruct
    private void consume() {
        while (true) {
            try {
                dispose(msgQueue.take());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理每条消息
     */
    protected abstract void dispose(MsgWrapper msgWrapper);

    /**
     * 选择对应的模式放入队列
     */
    public static void chooseAndQueue(PushWrapper pw) throws InterruptedException {
        Pattern pattern = PatternEnum.valueOf(pw.getPattern()).getPattern();
        pattern.getMsgQueue().put(pw.getMsgWrapper());
    }
}
