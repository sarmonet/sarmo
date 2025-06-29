package com.sarmo.userservice.producer;

import com.sarmo.kafka.dto.UserRegistrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdateProducer.class);
    private static final String TOPIC = "user-update";
    private final KafkaTemplate<String, UserRegistrationData> kafkaTemplate;

    public UserUpdateProducer(KafkaTemplate<String, UserRegistrationData> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение об обновлении данных пользователя в Kafka топик "user-update".
     * Реализован механизм повторных попыток с экспоненциальной задержкой.
     *
     * @param data Объект UserRegistrationData, содержащий обновленные данные пользователя.
     */
    public void sendUserUpdateMessage(UserRegistrationData data) {
        int maxRetries = 3;
        int retryCount = 0;
        long delay = 1000; // Начальная задержка в 1 секунду

        while (retryCount < maxRetries) {
            try {
                kafkaTemplate.send(TOPIC, data);
                logger.info("Message sent to Kafka topic {}: {}", TOPIC, data);
                return;
            } catch (KafkaException e) {
                logger.error("Failed to send message to Kafka topic {}: {}, retryCount: {}", TOPIC, data, retryCount, e);
                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(delay);
                        delay *= 2;
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        logger.error("Thread interrupted during retry delay", ex);
                        return;
                    }
                }
            }
        }
        logger.error("Failed to send message to Kafka topic {} after {} retries: {}", TOPIC, maxRetries, data);
    }
}