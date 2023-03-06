package com.kiligz.kzp.pusher.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ivan
 * @since 2022/12/28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Email extends AbstractMessage {
    String subject;

    public Email(String to, String content, String subject) {
        super(to, content);
        this.subject = subject;
    }
}
