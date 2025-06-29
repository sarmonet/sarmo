package com.sarmo.notificationservice.service;


import com.sarmo.notificationservice.enums.SenderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmService {

    private final EmailService emailService;

    @Value("${project.domain}")
    private String domain;

    // Конструктор для внедрения зависимости
    @Autowired
    public EmailConfirmService(EmailService emailService,
                               @Value("${spring.mail.senders.sender_confirm.username}") String senderEmail) {
        this.emailService = emailService;
    }

    // Метод для отправки email с подтверждением почты
    public void sendEmailConfirmation(String email, String token) {
        // Формируем ссылку для подтверждения почты
        String confirmationLink = "https://" + domain + "/api/v1/users/email/confirm?token=" + token;

        // Отправляем email через EmailService
        emailService.sendEmail(SenderType.SENDER_CONFIRM, email, "Email Confirmation",
                "Please confirm your email by clicking the link: " + confirmationLink);
    }

    public void sendPasswordResetEmail(String email, String token) {
        emailService.sendEmail(SenderType.SENDER_CONFIRM, email, "Password Reset",
                "Code to reset your password: " + token);
    }

    // Метод для отправки OTP на email
    public void sendOtpEmail(String email, String otp) {
        // Формируем сообщение с OTP
        String message = "Your one-time password (OTP) is: " + otp + "\nThis OTP is valid for 5 minutes.";

        // Отправляем email через EmailService
        emailService.sendEmail(SenderType.SENDER_CONFIRM, email, "OTP Confirmation", message);
    }
}
