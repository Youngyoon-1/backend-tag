package com.tag.application.image;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.responses.CreatePreauthenticatedRequestResponse;
import com.tag.dto.response.member.MemberImageUploadUrlResponse;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public final class OracleObjectManager extends ObjectStorageManager {

    private static final String REQUEST_KEY_NAME = "Request: ";
    private static final String TIME_KEY_NAME = " time: ";

    private final ObjectStorageClient objectStorageClient;
    private final ImagePathProvider imagePathProvider;
    private final String bucketName;
    private final String nameSpace;
    private final long expireLength;
    private final String endPoint;
    public OracleObjectManager(final ObjectStorageClient objectStorageClient,
                               final ImagePathProvider imagePathProvider,
                               @Value("${oci.object-storage.bucket-name}") final String bucketName,
                               @Value("${oci.object-storage.namespace}") final String nameSpace,
                               @Value("${object-storage.presigned-url-expire-length}") final long expireLength,
                               @Value("${oci.object-storage.endpoint}") final String endPoint) {
        this.objectStorageClient = objectStorageClient;
        this.imagePathProvider = imagePathProvider;
        this.bucketName = bucketName;
        this.nameSpace = nameSpace;
        this.expireLength = expireLength;
        this.endPoint = endPoint;
    }

    @Override
    protected String createPresignedGetUrl(final String imageName) {
        final String imagePath = imagePathProvider.create(imageName);
        return endPoint + createPreauthenticatedRequestUrl(imagePath, AccessType.ObjectRead);
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
        return response.getPreauthenticatedRequest()
                .getAccessUri();
    }

    private String generateRequestName(final String imagePath, final AccessType accessType) {
        return accessType.name()
                + REQUEST_KEY_NAME
                + imagePath
                + TIME_KEY_NAME
                + new Date();
    }

    @Override
    public MemberImageUploadUrlResponse createPutUrl(final String fileType) {
        final String imageName = UUID.randomUUID()
                .toString();
        final String imagePath = imagePathProvider.create(imageName);
        final String presignedUrl = endPoint + createPreauthenticatedRequestUrl(imagePath, AccessType.ObjectWrite);
        return new MemberImageUploadUrlResponse(presignedUrl, imageName);
    }

    @Override
    public void deleteObject(final String profileImageName) {
        final String imagePath = imagePathProvider.create(profileImageName);
        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .namespaceName(nameSpace)
                .bucketName(bucketName)
                .objectName(imagePath)
                .build();
        objectStorageClient.deleteObject(deleteObjectRequest);
    }
}
