package com.tag.acceptance;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public abstract class WithTestcontainers {

    static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:7.2.3-alpine"))
            .withExposedPorts(6379);

    @ServiceConnection
    static MySQLContainer mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"));

    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.0.2"))
            .withServices(S3);

    static {
        redis.start();
        mysql.start();
        // URL 만료 테스트르 위한 환경변수 설정
        localstack.withEnv("S3_SKIP_SIGNATURE_VALIDATION", "0");
        localstack.start();
        // s3 presignedUrl 을 사용하므로 업로드는 클라이언트에서 이루어진다.
        // 테스트 환경에서도 실제로 유효한 presignedUrl 을 응답하기 위해 미리 s3 에 사용될 이미지를 업로드한다.
        try {
            final S3Client s3 = S3Client.builder()
                    .endpointOverride(localstack.getEndpoint())
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                            )
                    )
                    .region(Region.of(localstack.getRegion()))
                    .build();

            // s3 test-bucket 생성
            s3.createBucket(CreateBucketRequest.builder()
                    .bucket("test-bucket")
                    .build()
            );

            // 클래스 로더를 통해 이미지 파일의 URL 을 가져온다.
            final URI resource = ClassLoader.getSystemClassLoader()
                    .getResource("tag.png")
                    .toURI();

            // s3 test-bucket 의 profile-image 폴더에 profileImageName 이미지 업로드
            final Path imagePath = Path.of(resource);
            PutObjectRequest putOb1 = PutObjectRequest.builder()
                    .bucket("test-bucket")
                    .key("profile-image/profileImageName")
                    .build();
            s3.putObject(putOb1, RequestBody.fromFile(imagePath));

            // s3 test-bucket 의 qr-image 폴더에 qrImageName 이미지 업로드 profileImageName 이미지와 동일한 이미지 사용
            PutObjectRequest putOb2 = PutObjectRequest.builder()
                    .bucket("test-bucket")
                    .key("qr-image/qrImageName")
                    .build();
            s3.putObject(putOb2, RequestBody.fromFile(imagePath));
        } catch (final S3Exception | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
    }

    @DynamicPropertySource
    public static void overrideProps(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> "" + redis.getMappedPort(6379));
        registry.add("aws.access-key", () -> localstack.getAccessKey());
        registry.add("aws.secret-access-key", () -> localstack.getSecretKey());
        registry.add("aws.s3.region", () -> localstack.getRegion());
        registry.add("aws.s3.end-point", () -> localstack.getEndpoint());
    }
}
