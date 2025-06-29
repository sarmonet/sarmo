package com.sarmo.notificationservice.grpc;

import com.sarmo.notificationservice.proto.EmailConfirmServiceGrpc;
import com.sarmo.notificationservice.proto.EmailRequest;
import com.sarmo.notificationservice.proto.EmailResponse;
import com.sarmo.notificationservice.service.EmailConfirmService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@GrpcService
public class EmailConfirmGrpcService extends EmailConfirmServiceGrpc.EmailConfirmServiceImplBase {

    private final EmailConfirmService emailConfirmService;

    @Autowired
    public EmailConfirmGrpcService(EmailConfirmService emailConfirmService) {
        this.emailConfirmService = emailConfirmService;
    }

    @Override
    public void sendEmailConfirmation(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        try {
            emailConfirmService.sendEmailConfirmation(request.getEmail(), request.getToken());

            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Email sent successfully")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to send email: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendPasswordResetEmail(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        try {
            emailConfirmService.sendPasswordResetEmail(request.getEmail(), request.getToken());

            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Password reset email sent successfully")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to send password reset email: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void sendOtpEmail(EmailRequest request, StreamObserver<EmailResponse> responseObserver) {
        try {
            // Генерируем OTP и отправляем его через EmailConfirmService
            emailConfirmService.sendOtpEmail(request.getEmail(), request.getToken());

            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("OTP sent successfully")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            EmailResponse response = EmailResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to send OTP: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }
}
