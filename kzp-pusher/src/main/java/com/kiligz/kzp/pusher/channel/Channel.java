package com.kiligz.kzp.pusher.channel;

import com.kiligz.kzp.pusher.domain.AbstractMessage;

/**
 * @author Ivan
 * @date 2022/12/28 16:56
 */
public interface Channel {
    void send(AbstractMessage abstractMessage);
}
