package ru.andryss.trousseau.service;

import java.util.concurrent.TimeUnit;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andryss.trousseau.config.S3Properties;

@Slf4j
@Service
@Profile("!functionalTest")
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service, InitializingBean, DisposableBean {

    private final S3Properties properties;
    private MinioClient client;

    private static final int DEFAULT_URL_EXPIRATION_MINUTES = 300;


    @Override
    public void afterPropertiesSet() {
        client = MinioClient.builder()
                .endpoint(properties.getUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucket()).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            }
        } catch (Exception e) {
            log.error("Error while creating bucket", e);
            throw new RuntimeException(e);
        }
        log.info("S3 client successfully created with default bucket {}", properties.getBucket());
    }

    @Override
    public void destroy() throws Exception {
        client.close();
    }

    @Override
    public void put(String path, MultipartFile file) {
        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(path)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error while uploading object", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String presignedUrl(String path) {
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(properties.getBucket())
                            .object(path)
                            .method(Method.GET)
                            .expiry(DEFAULT_URL_EXPIRATION_MINUTES, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error while generating presigned url", e);
            throw new RuntimeException(e);
        }
    }
}
