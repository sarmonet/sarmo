package com.sarmo.notificationservice.grpc;

import com.sarmo.notificationservice.proto.NotificationRequest;
import com.sarmo.notificationservice.proto.NotificationResponse;
import com.sarmo.notificationservice.proto.NotificationServiceGrpc;
import com.sarmo.notificationservice.service.EmailService;
import com.sarmo.notificationservice.enums.SenderType;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@GrpcService
public class NotificationGrpcService extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final EmailService emailService;

    @Autowired
    public NotificationGrpcService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendNewListingsNotification(NotificationRequest request, StreamObserver<NotificationResponse> responseObserver) {
        try {
            emailService.sendEmail(SenderType.SENDER_NOTIFICATION, request.getEmail(), request.getSubject(), request.getBody());

            NotificationResponse response = NotificationResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Notification email sent successfully")
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            NotificationResponse response = NotificationResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to send notification email: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
        } finally {
            responseObserver.onCompleted();
        }
    }
}