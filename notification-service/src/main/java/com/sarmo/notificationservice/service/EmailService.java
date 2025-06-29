package com.sarmo.notificationservice.service;

import com.sarmo.notificationservice.config.MailConfig;
import com.sarmo.notificationservice.enums.SenderType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailService {

    private final Map<SenderType, JavaMailSender> emailSenderMap;
    private final Map<SenderType, String> senderEmailMap;

    public EmailService(
            @Qualifier("sender_confirm") JavaMailSender senderConfirm,
            @Qualifier("sender_notification") JavaMailSender senderNotification,
            @Qualifier("sender_promo") JavaMailSender senderPromo,
            @Value("${spring.mail.senders.sender_confirm.username}") String senderConfirmEmail,
            @Value("${spring.mail.senders.sender_notification.username}") String senderNotificationEmail,
            @Value("${spring.mail.senders.sender_promo.username}") String senderPromoEmail
    ) {
        emailSenderMap = Map.of(
                SenderType.SENDER_CONFIRM, senderConfirm,
                SenderType.SENDER_NOTIFICATION, senderNotification,
                SenderType.SENDER_PROMO, senderPromo
        );

        senderEmailMap = Map.of(
                SenderType.SENDER_CONFIRM, senderConfirmEmail,
                SenderType.SENDER_NOTIFICATION, senderNotificationEmail,
                SenderType.SENDER_PROMO, senderPromoEmail
        );
    }

    public void sendEmail(SenderType senderType, String to, String subject, String text) {
        JavaMailSender emailSender = emailSenderMap.get(senderType);
        String fromAddress = senderEmailMap.get(senderType);

        if (emailSender == null || fromAddress == null) {
            throw new IllegalArgumentException("Invalid sender type or configuration.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        emailSender.send(message);
    }
}
