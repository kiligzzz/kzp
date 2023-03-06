package com.kiligz.kzp.processor.pattern.impl;

import com.kiligz.kzp.common.domain.MsgWrapper;
import com.kiligz.kzp.processor.pattern.Pattern;
import org.springframework.stereotype.Service;

/**
 * 直接推送模式
 *
 * @author Ivan
 * @since 2022/12/30
 */
@Service
public class DirectPattern extends Pattern {
    @Override
    protected void dispose(MsgWrapper msgWrapper) {

    }
}
