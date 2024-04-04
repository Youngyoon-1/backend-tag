package com.tag.config;

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.tag.application.ImagePathProvider;
import com.tag.application.ObjectStorageManager;
import com.tag.application.OracleObjectManager;
import com.tag.application.S3ObjectManager;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class ObjectStorageConfig {

    @Bean
    @ConditionalOnProperty(name = "object-storage.vendor-name", havingValue = "oracle")
    public ObjectStorageManager oracleObjectManager(final ObjectStorageClient objectStorageClient,
                                                    final ImagePathProvider imagePathProvider,
                                                    @Value("${oci.object-storage.bucket-name}") final String bucketName,
                                                    @Value("${oci.object-storage.namespace}") final String nameSpace,
                                                    @Value("${object-storage.presigned-url-expire-length}") final long expireLength,
                                                    @Value("${oci.object-storage.endpoint}") final String endpoint) {
        return new OracleObjectManager(objectStorageClient, imagePathProvider, bucketName, nameSpace,
                expireLength, endpoint);
    }

    @Bean
    @ConditionalOnProperty(name = "object-storage.vendor-name", havingValue = "oracle")
    public ObjectStorageClient objectStorageClient(@Value("${oci.object-storage.region}") final String region,
                                                   @Value("${oci.object-storage.tenancy-id}") final String tenancyId,
                                                   @Value("${oci.object-storage.user-id}") final String userId,
                                                   @Value("${oci.object-storage.fingerprint}") final String fingerprint,
                                                   @Value("${oci.object-storage.private-key}") final String privateKey) {
        final String parsedPrivateKey = privateKey.replace("\\n", "\n");
        try {
            SimpleAuthenticationDetailsProvider provider =
                    SimpleAuthenticationDetailsProvider.builder()
                            .tenantId(tenancyId)
                            .userId(userId)
                            .fingerprint(fingerprint)
                            .privateKeySupplier(
                                    () -> new ByteArrayInputStream(parsedPrivateKey.getBytes(StandardCharsets.UTF_8)))
                            .build();
            return ObjectStorageClient.builder()
                    .region(region)
                    .build(provider);
        } catch (
                final Exception e) {
            throw new RuntimeException("Failed to create Oracle ObjectStorageClient", e);
        }
    }

    @Bean
    @ConditionalOnProperty(name = "object-storage.vendor-name", havingValue = "aws")
    public ObjectStorageManager s3ObjectManager(final S3Presigner s3Presigner,
                                                final S3Client s3Client,
                                                final ImagePathProvider imagePathProvider,
                                                @Value("${aws.s3.bucket-name}") final String bucketName,
                                                @Value("${object-storage.presigned-url-expire-length}") final long expireLength) {
        return new S3ObjectManager(s3Presigner, s3Client, imagePathProvider, bucketName, expireLength);
    }

    @Bean
    @ConditionalOnProperty(name = "object-storage.vendor-name", havingValue = "aws")
    public S3Presigner s3Presigner(@Value("${aws.access-key}") final String accessKey,
                                   @Value("${aws.secret-access-key}") final String secretKey,
                                   @Value("${aws.s3.region}") final String region,
                                   // 테스트 환경에서 end-point override 를 위해 설정
                                   @Value("${aws.s3.end-point}") final String endPoint) {
        final AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
                .endpointOverride(URI.create(endPoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "object-storage.vendor-name", havingValue = "aws")
    public S3Client s3Client(@Value("${aws.access-key}") final String accessKey,
                             @Value("${aws.secret-access-key}") final String secretKey,
                             @Value("${aws.s3.region}") final String region,
                             // 테스트 환경에서 end-point override 를 위해 설정
                             @Value("${aws.s3.end-point}") final String endPoint) {
        final AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .endpointOverride(URI.create(endPoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }
}
