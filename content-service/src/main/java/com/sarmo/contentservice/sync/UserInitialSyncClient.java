package com.sarmo.contentservice.sync;

import com.sarmo.contentservice.entity.User;
import com.sarmo.contentservice.repository.UserRepository;
import com.sarmo.contentservice.service.UserBatchSaveService;
import com.sarmo.grpc.user.SyncRequest;
import com.sarmo.grpc.user.UserDataSyncServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserInitialSyncClient {

    private static final Logger logger = LoggerFactory.getLogger(UserInitialSyncClient.class);

    @GrpcClient("user-service")
    private UserDataSyncServiceGrpc.UserDataSyncServiceStub userServiceSyncStub;

    private final UserBatchSaveService userBatchSaveService;

    private final UserRepository userRepository;

    private final List<User> batchBuffer = new ArrayList<>();

    private final int BATCH_SIZE = 100;

    public UserInitialSyncClient(UserBatchSaveService userBatchSaveService, UserRepository userRepository) {
        this.userBatchSaveService = userBatchSaveService;
        this.userRepository = userRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (userRepository.count() == 0) {
            logger.info("User data table is empty. Starting initial sync via gRPC...");
            performSync();
        } else {
            logger.info("User data table is not empty. Attempting to sync/update existing users.");
            performSync();
        }
    }

    private void performSync() {
        if (userServiceSyncStub == null) {
            logger.error("@GrpcClient('user-service') was not injected. Cannot perform sync.");
            return;
        }

        userServiceSyncStub.streamAllUsers(SyncRequest.newBuilder().build(), new StreamObserver<com.sarmo.grpc.user.User>() {
            private int receivedCount = 0;

            @Override
            public void onNext(com.sarmo.grpc.user.User protoUser) {
                logger.debug("Received user with ID: {}", protoUser.getId());

                try {
                    Long userId = protoUser.getId();

                    Optional<User> existingUserOptional = userRepository.findById(userId);
                    User userEntity;

                    if (existingUserOptional.isPresent()) {
                        userEntity = existingUserOptional.get();
                        logger.debug("User with ID {} found in local DB. Will update.", userId);
                        userEntity.setFirstName(protoUser.getFirstName());
                        userEntity.setLastName(protoUser.getLastName());
                        userEntity.setProfilePictureUrl(protoUser.getProfilePictureUrl());

                        protoUser.getEmail();
                        if (!protoUser.getEmail().isEmpty()) {
                            userEntity.setEmail(protoUser.getEmail());
                        } else {
                            userEntity.setEmail(null);
                        }

                        protoUser.getPhoneNumber();
                        if (!protoUser.getPhoneNumber().isEmpty()) {
                            userEntity.setPhoneNumber(protoUser.getPhoneNumber());
                        } else {
                            userEntity.setPhoneNumber(null);
                        }


                    } else {
                        userEntity = new User();
                        userEntity.setId(userId);
                        userEntity.setFirstName(protoUser.getFirstName());
                        userEntity.setLastName(protoUser.getLastName());
                        userEntity.setProfilePictureUrl(protoUser.getProfilePictureUrl());

                        protoUser.getEmail();
                        if (!protoUser.getEmail().isEmpty()) {
                            userEntity.setEmail(protoUser.getEmail());
                        } else {
                            userEntity.setEmail(null);
                        }

                        protoUser.getPhoneNumber();
                        if (!protoUser.getPhoneNumber().isEmpty()) {
                            userEntity.setPhoneNumber(protoUser.getPhoneNumber());
                        } else {
                            userEntity.setPhoneNumber(null);
                        }

                        logger.debug("User with ID {} not found in local DB. Will create.", userId);
                    }

                    batchBuffer.add(userEntity);

                    if (batchBuffer.size() >= BATCH_SIZE) {
                        saveBatch();
                    }

                    receivedCount++;
                } catch (Exception e) {
                    logger.error("Failed to process received user with ID: {}", protoUser.getId(), e);
                }
            }

            private void saveBatch() {
                if (!batchBuffer.isEmpty()) {
                    logger.info("Saving batch of {} users via transactional service...", batchBuffer.size());
                    try {
                        userBatchSaveService.saveBatch(new ArrayList<>(batchBuffer));
                        logger.info("Batch saved successfully.");
                    } catch (Exception e) {
                        logger.error("Error saving batch of users", e);
                    } finally {
                        batchBuffer.clear();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.error("User data sync stream failed", t);
                saveBatch();
            }

            @Override
            public void onCompleted() {
                saveBatch();

                logger.info("User data sync stream completed successfully. Received and processed {} users.", receivedCount);
            }
        });
    }
}