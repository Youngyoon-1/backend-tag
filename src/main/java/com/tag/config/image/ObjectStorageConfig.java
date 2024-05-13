package com.tag.config.image;

import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class ObjectStorageConfig {

    private static final String ESCAPED_NEW_LINE = "\\n";
    private static final String NEW_LINE = "\n";

    @Bean
    public ObjectStorageClient objectStorageClient(@Value("${oci.object-storage.region}") final String region,
                                                   @Value("${oci.object-storage.tenancy-id}") final String tenancyId,
                                                   @Value("${oci.object-storage.user-id}") final String userId,
                                                   @Value("${oci.object-storage.fingerprint}") final String fingerprint,
                                                   @Value("${oci.object-storage.private-key}") final String privateKey) {
        final byte[] parsedPrivateKey = privateKey.replace(ESCAPED_NEW_LINE, NEW_LINE)
                .getBytes(StandardCharsets.UTF_8);
        final SimpleAuthenticationDetailsProvider provider =
                SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(tenancyId)
                        .userId(userId)
                        .fingerprint(fingerprint)
                        .privateKeySupplier(
                                () -> new ByteArrayInputStream(parsedPrivateKey))
                        .build();
        return ObjectStorageClient.builder()
                .region(region)
                .build(provider);
    }
}
