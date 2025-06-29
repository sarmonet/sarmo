package com.sarmo.notificationservice.service;

import com.sarmo.notificationservice.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    private final TwilioConfig twilioConfig;

    public TwilioSmsService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    public String sendSms(String phoneNumber, String messageText) {
        try {
            // Инициализируем Twilio (только 1 раз)
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());

            // Отправляем SMS
            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    messageText
            ).create();

            return "SMS sent successfully! Message SID: " + message.getSid();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }
}
