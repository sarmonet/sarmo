package com.sarmo.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {


    @Bean(name = "sender_confirm")
    public JavaMailSender senderConfirm(
            @Value("${spring.mail.senders.sender_confirm.host}") String host,
            @Value("${spring.mail.senders.sender_confirm.port}") int port,
            @Value("${spring.mail.senders.sender_confirm.username}") String username,
            @Value("${spring.mail.senders.sender_confirm.password}") String password
    ) {
        return createMailSender(host, port, username, password);
    }

    @Bean(name = "sender_notification")
    public JavaMailSender senderNotification(
            @Value("${spring.mail.senders.sender_notification.host}") String host,
            @Value("${spring.mail.senders.sender_notification.port}") int port,
            @Value("${spring.mail.senders.sender_notification.username}") String username,
            @Value("${spring.mail.senders.sender_notification.password}") String password
    ) {
        return createMailSender(host, port, username, password);
    }

    @Bean(name = "sender_promo")
    public JavaMailSender senderPromo(
            @Value("${spring.mail.senders.sender_promo.host}") String host,
            @Value("${spring.mail.senders.sender_promo.port}") int port,
            @Value("${spring.mail.senders.sender_promo.username}") String username,
            @Value("${spring.mail.senders.sender_promo.password}") String password
    ) {
        return createMailSender(host, port, username, password);
    }

    private JavaMailSender createMailSender(String host, int port, String username, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);
        return mailSender;
    }
}
