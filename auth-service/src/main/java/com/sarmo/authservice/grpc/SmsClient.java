package com.sarmo.authservice.grpc;

import com.sarmo.notificationservice.proto.SmsRequest;
import com.sarmo.notificationservice.proto.SmsResponse;
import com.sarmo.notificationservice.proto.SmsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class SmsClient {

    @GrpcClient("notification-service") // Имя должно совпадать с именем сервиса в конфиге
    private SmsServiceGrpc.SmsServiceBlockingStub smsServiceStub;

    public boolean sendSms(String phoneNumber, String message) {
        SmsRequest request = SmsRequest.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setMessage(message)
                .build();

        SmsResponse response = smsServiceStub.sendSms(request);
        return response.getSuccess();
    }
}
