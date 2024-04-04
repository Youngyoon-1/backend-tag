package com.tag.application;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.oracle.bmc.objectstorage.responses.DeleteObjectResponse;
import com.tag.dto.response.MemberImageUploadUrlResponse;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OracleObjectManager implements ObjectStorageManager {

    private final ObjectStorageClient objectStorageClient;
    private final ImagePathProvider imagePathProvider;
    private final String bucketName;
    private final String nameSpace;
    private final long expireLength;
    private final String endPoint;

    public OracleObjectManager(final ObjectStorageClient objectStorageClient,
                               final ImagePathProvider imagePathProvider,
                               final String bucketName, final String nameSpace, final long expireLength,
                               final String endPoint) {
        this.objectStorageClient = objectStorageClient;
        this.imagePathProvider = imagePathProvider;
        this.bucketName = bucketName;
        this.nameSpace = nameSpace;
        this.expireLength = expireLength;
        this.endPoint = endPoint;
    }

    @Override
    public String createPresignedGetUrl(final String imageName, final MemberImageCategory memberImageCategory) {
        final String imagePath = imagePathProvider.create(imageName, memberImageCategory);
        return endPoint + createPreauthenticatedRequestUrl(imagePath, AccessType.ObjectRead);
    }

    @Override
    public MemberImageUploadUrlResponse createPresignedPutUrl(final MemberImageCategory memberImageCategory,
                                                              final String fileType) {
        final String imageName = UUID.randomUUID()
                .toString();
        final String imagePath = imagePathProvider.create(imageName, memberImageCategory);
        final String presignedUrl = endPoint + createPreauthenticatedRequestUrl(imagePath, AccessType.ObjectWrite);
        return new MemberImageUploadUrlResponse(presignedUrl, imageName);
    }

    @Override
    public void deleteObject(final String profileImageName) {
        final String imagePath = imagePathProvider.create(profileImageName, MemberImageCategory.PROFILE);
        try {
            final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .namespaceName(nameSpace)
                    .bucketName(bucketName)
                    .objectName(imagePath)
                    .build();
            final DeleteObjectResponse deleteObjectResponse = objectStorageClient.deleteObject(deleteObjectRequest);
            if (deleteObjectResponse.get__httpStatusCode__() < 200
                    || deleteObjectResponse.get__httpStatusCode__() >= 300) {
                log.error("Failed to delete image from Oracle Object Storage, ImageName : {}", profileImageName);
            }
        } catch (final Exception e) {
            log.error("Failed to delete image from Oracle Object Storage, Message : {}, ImageName : {}", e.getMessage(),
                    profileImageName);
        }
    }

    private String createPreauthenticatedRequestUrl(final String imagePath, final AccessType accessType) {
        final CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .name(generateRequestName(imagePath, accessType))
                .accessType(accessType)
                .timeExpires(Date.from(Instant.now().plusMillis(expireLength)))
                .objectName(imagePath)
                .build();

        final CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(nameSpace)
                .bucketName(bucketName)
                .createPreauthenticatedRequestDetails(details)
                .build();

        final CreatePreauthenticatedRequestResponse response = objectStorageClient.createPreauthenticatedRequest(
                request);
        return response.getPreauthenticatedRequest().getAccessUri();
    }

    private String generateRequestName(final String imagePath, final AccessType accessType) {
        return accessType.name() + "Request: " + imagePath + " time: " + Date.from(Instant.now());
    }
}
