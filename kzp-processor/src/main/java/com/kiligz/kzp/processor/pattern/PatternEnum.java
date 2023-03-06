package com.kiligz.kzp.processor.pattern;

import com.kiligz.kzp.common.utils.SpringUtil;
import com.kiligz.kzp.processor.pattern.impl.DirectPattern;
import com.kiligz.kzp.processor.pattern.impl.ScheduledPattern;
import com.kiligz.kzp.processor.pattern.impl.TriggeredPattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息触发模式枚举
 *
 * @author Ivan
 * @since 2022/01/27
 */
@Getter
@AllArgsConstructor
public enum PatternEnum {
    DIRECT(SpringUtil.getBean(DirectPattern.class)),
    SCHEDULED(SpringUtil.getBean(ScheduledPattern.class)),
    TRIGGERED(SpringUtil.getBean(TriggeredPattern.class));

    Pattern pattern;
}