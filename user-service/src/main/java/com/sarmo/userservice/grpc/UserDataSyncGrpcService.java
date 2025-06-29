package com.sarmo.userservice.grpc;

import com.sarmo.grpc.user.SyncRequest;
import com.sarmo.grpc.user.User;
import com.sarmo.grpc.user.UserDataSyncServiceGrpc;
import com.sarmo.userservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;

@GrpcService
public class UserDataSyncGrpcService extends UserDataSyncServiceGrpc.UserDataSyncServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(UserDataSyncGrpcService.class);

    private final UserRepository userRepository;

    public UserDataSyncGrpcService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void streamAllUsers(SyncRequest request, StreamObserver<User> responseObserver) {
        logger.info("Received user data sync request from client.");

        // Ensure findAll() returns Iterable or List and use iterator
        Iterator<com.sarmo.userservice.entity.User> userIterator = userRepository.findAll().iterator();

        try {
            while (userIterator.hasNext()) {
                com.sarmo.userservice.entity.User userEntity = userIterator.next();

                User.Builder protoUserBuilder = User.newBuilder()
                        .setId(Optional.ofNullable(userEntity.getId()).orElse(0L))
                        .setFirstName(Optional.ofNullable(userEntity.getFirstName()).orElse(""))
                        .setLastName(Optional.ofNullable(userEntity.getLastName()).orElse(""))
                        .setProfilePictureUrl(Optional.ofNullable(userEntity.getProfilePictureUrl()).orElse(""));


                if (userEntity.getEmail() != null) {
                    protoUserBuilder.setEmail(userEntity.getEmail());
                } else {
                    protoUserBuilder.setEmail("");
                }

                if (userEntity.getPhoneNumber() != null) {
                    protoUserBuilder.setPhoneNumber(userEntity.getPhoneNumber());
                } else {
                    protoUserBuilder.setPhoneNumber("");
                }

                User protoUser = protoUserBuilder.build();


                responseObserver.onNext(protoUser);
            }

            responseObserver.onCompleted(); // Signal to the client that data transmission is complete
            logger.info("Finished streaming all users successfully.");

        } catch (Exception e) {
            // Handle errors during DB reading or data sending
            logger.error("Error during streaming user data", e);
            // Send an error signal to the client
            // Consider mapping exception to gRPC status for more granular errors
            responseObserver.onError(e);
        }
    }
}