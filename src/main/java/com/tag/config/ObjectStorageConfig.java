package com.tag.config;

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.tag.application.ImagePathProvider;
import com.tag.application.ObjectStorageManager;
import com.tag.application.OracleObjectManager;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            final SimpleAuthenticationDetailsProvider provider =
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
}
