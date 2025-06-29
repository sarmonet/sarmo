//package com.sarmo.authservice.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Bean
//    public NewTopic userRegistrationTopic() {
//        return TopicBuilder.name("user-registration")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic userReferralRegistrationTopic() {
//        return TopicBuilder.name("user-referral-registration")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//}