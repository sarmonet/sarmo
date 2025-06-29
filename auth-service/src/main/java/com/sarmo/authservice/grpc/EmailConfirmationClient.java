package com.sarmo.authservice.grpc;

import com.sarmo.notificationservice.proto.EmailConfirmServiceGrpc;
import com.sarmo.notificationservice.proto.EmailRequest;
import com.sarmo.notificationservice.proto.EmailResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmationClient {

    @GrpcClient("notification-service")
    private EmailConfirmServiceGrpc.EmailConfirmServiceBlockingStub emailConfirmServiceStub;

    public void sendEmailConfirmation(String email, String token) {
        EmailRequest request = EmailRequest.newBuilder()
                .setEmail(email)
                .setToken(token)
                .build();

        // Отправляем запрос на сервер gRPC
        EmailResponse response = emailConfirmServiceStub.sendEmailConfirmation(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("Failed to send email confirmation: " + response.getMessage());
        }
    }

    public void sendPasswordResetConfirmation(String email, String token) {
        EmailRequest request = EmailRequest.newBuilder()
                .setEmail(email)
                .setToken(token)
                .build();

        // Отправляем запрос на сервер gRPC для подтверждения смены пароля
        EmailResponse response = emailConfirmServiceStub.sendPasswordResetEmail(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("Failed to send password reset confirmation: " + response.getMessage());
        }
    }

    public void sendOtpEmail(String email, String otp) {
        EmailRequest request = EmailRequest.newBuilder()
                .setEmail(email)
                .setToken(otp)  // В этом случае используем OTP как token
                .build();

        // Отправляем запрос на сервер gRPC для отправки OTP
        EmailResponse response = emailConfirmServiceStub.sendOtpEmail(request);

        if (!response.getSuccess()) {
            throw new RuntimeException("Failed to send OTP: " + response.getMessage());
        }
    }
}
