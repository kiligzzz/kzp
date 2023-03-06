package com.kiligz.kzp.pusher.channel.email;

import com.kiligz.kzp.common.domain.MsgWrapper;
import com.kiligz.kzp.entity.user.User;
import com.kiligz.kzp.pusher.domain.AbstractMessage;
import com.kiligz.kzp.pusher.domain.Email;
import com.kiligz.kzp.pusher.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author Ivan
 * @since 2022/12/28
 */
@RequiredArgsConstructor
@Component
public class EmailChannel implements Channel {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void send(AbstractMessage abstractMessage) {
        Email mailMessage = (Email) abstractMessage;
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(from);
        //邮件接收人
        message.setTo(mailMessage.getTo());
        //邮件主题
        message.setSubject(mailMessage.getSubject());
        //邮件内容
        message.setText(mailMessage.getContent());
        //发送邮件
        mailSender.send(message);
        MsgWrapper mw = new MsgWrapper();
        User user = mw.getUser();
        user.setEmailQuota(user.getEmailQuota() - mw.getMsgList().size());
    }
}
