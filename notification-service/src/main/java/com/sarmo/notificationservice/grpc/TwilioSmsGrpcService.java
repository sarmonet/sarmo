package com.sarmo.notificationservice.grpc;

import com.sarmo.notificationservice.proto.SmsRequest;
import com.sarmo.notificationservice.proto.SmsResponse;
import com.sarmo.notificationservice.proto.SmsServiceGrpc;
import com.sarmo.notificationservice.service.TwilioSmsService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class TwilioSmsGrpcService extends SmsServiceGrpc.SmsServiceImplBase {

    private final TwilioSmsService twilioSmsService;

    public TwilioSmsGrpcService(TwilioSmsService twilioSmsService) {
        this.twilioSmsService = twilioSmsService;
    }

    @Override
    public void sendSms(SmsRequest request, StreamObserver<SmsResponse> responseObserver) {
        try {
            String responseMessage = twilioSmsService.sendSms(request.getPhoneNumber(), request.getMessage());

            SmsResponse response = SmsResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage(responseMessage)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            SmsResponse response = SmsResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
