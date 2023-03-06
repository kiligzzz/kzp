package com.kiligz.kzp.pusher;

import com.kiligz.kzp.common.utils.ConcurrentManager;
import com.kiligz.kzp.pusher.task.PushTask;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Ivan
 * @since 2022/12/28
 */
@Service
public class PushHandler {
    @PostConstruct
    private void init() {
        ConcurrentManager.setCorePoolSize(ConcurrentManager.CPU_INTENSIVE);
        ConcurrentManager.ThreadPool push = ConcurrentManager.getFixedThreadPool("push");
        push.execute(new PushTask(), ConcurrentManager.CPU_INTENSIVE);
    }
}
