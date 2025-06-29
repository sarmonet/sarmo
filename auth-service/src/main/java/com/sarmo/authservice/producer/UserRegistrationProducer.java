package com.sarmo.authservice.producer;

import com.sarmo.kafka.dto.UserRegistrationData;
import com.sarmo.kafka.dto.UserRegistrationWithReferralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.KafkaException;

@Service
public class UserRegistrationProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationProducer.class);
    private static final String TOPIC = "user-registration";
    private final KafkaTemplate<String, UserRegistrationData> kafkaTemplate;

    public UserRegistrationProducer(KafkaTemplate<String, UserRegistrationData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserRegistrationMessage(UserRegistrationData data) {
        int maxRetries = 3;
        int retryCount = 0;
        long delay = 1000; // Начальная задержка в миллисекундах

        while (retryCount < maxRetries) {
            try {
                kafkaTemplate.send(TOPIC, data);
                logger.info("Message sent to Kafka topic {}: {}", TOPIC, data);
                return; // Успешно отправлено, выходим из метода
            } catch (KafkaException e) {
                logger.error("Failed to send message to Kafka topic {}: {}, retryCount: {}", TOPIC, data, retryCount, e);
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(delay);
                        delay *= 2; // Увеличиваем задержку вдвое
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.error("Thread interrupted during retry delay", ex);
                        return; // Прерывание, выходим из метода
                    }
                }
            }
        }
        logger.error("Failed to send message to Kafka topic {} after {} retries: {}", TOPIC, maxRetries, data);
    }
}