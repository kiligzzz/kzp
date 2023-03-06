package com.kiligz.kzp.pusher.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ivan
 * @since 2022/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractMessage {
    String to;
    String content;
}
