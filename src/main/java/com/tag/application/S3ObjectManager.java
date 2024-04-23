package com.tag.application;

import com.tag.dto.response.MemberImageUploadUrlResponse;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
public class S3ObjectManager implements ObjectStorageManager {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final ImagePathProvider imagePathProvider;
    private final String bucketName;
    private final long expireLength;

    public S3ObjectManager(final S3Presigner s3Presigner, final S3Client s3Client,
                           final ImagePathProvider imagePathProvider,
                           final String bucketName, final long expireLength) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
        this.imagePathProvider = imagePathProvider;
        this.bucketName = bucketName;
        this.expireLength = expireLength;
    }

    @Override
    public String createPresignedGetUrl(final String imageName, final MemberImageCategory memberImageCategory) {
        final String imagePath = imagePathProvider.create(imageName, memberImageCategory);
        final GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imagePath)
                .build();
        final GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMillis(expireLength))
                .getObjectRequest(objectRequest)
                .build();
        return s3Presigner.presignGetObject(presignRequest)
                .url()
                .toExternalForm();
    }

    @Override
    public MemberImageUploadUrlResponse createPresignedPutUrl(final MemberImageCategory memberImageCategory,
                                                              final String fileType) {
        final String imageName = UUID.randomUUID()
                .toString();
        final String imagePath = imagePathProvider.create(imageName, memberImageCategory);
        final PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imagePath)
                .contentType(fileType)
                .build();
        final PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMillis(expireLength))
                .putObjectRequest(objectRequest)
                .build();
        final String presignedUrl = s3Presigner.presignPutObject(presignRequest)
                .url()
                .toExternalForm();
        return new MemberImageUploadUrlResponse(presignedUrl, imageName);
    }

    @Override
    public void deleteObject(final String profileImageName) {
        final String imagePath = imagePathProvider.create(profileImageName, MemberImageCategory.PROFILE);
        try {
            final DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(
                    builder -> builder.bucket(bucketName)
                            .key(imagePath));
            if (!deleteObjectResponse.sdkHttpResponse().isSuccessful()) {
                log.error("Failed to delete image from S3, ImageName : {}", profileImageName);
            }
        } catch (final Exception e) {
            log.error("Failed to delete image from S3, Message : {}, ImageName : {}", e.getMessage(), profileImageName);
        }
    }
}
