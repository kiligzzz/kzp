package com.kiligz.kzp.common.domain;

import lombok.Data;

/**
 * @author Ivan
 * @since 2022/12/30
 */
@Data
public class PushWrapper {
    String pattern;
    MsgWrapper msgWrapper;
}
