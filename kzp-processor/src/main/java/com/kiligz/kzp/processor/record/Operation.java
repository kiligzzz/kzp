package com.kiligz.kzp.processor.record;

import com.kiligz.kzp.common.domain.MsgWrapper;

/**
 * @author Ivan
 * @date 2023/2/7 17:05
 */
public interface Operation {
    void accept(MsgWrapper msgWrapper);
}
