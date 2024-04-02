package it.pagopa.atmlayer.wf.task.util;

import java.util.concurrent.CompletableFuture;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Getter
@Singleton
public class FileStorageS3Util {

    @Inject
    Properties properties;

    BlockingInputStreamAsyncRequestBody body;

    CompletableFuture<PutObjectResponse> responseFuture;

    S3AsyncClient s3Client;

    @PostConstruct
    public void init() {
        s3Client = S3AsyncClient.crtBuilder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(properties.bucket().region()))
            .build();

        body = AsyncRequestBody.forBlockingInputStream(null);

        responseFuture = s3Client.putObject(r -> r.bucket(properties.bucket().name())
                .key(properties.resource().pathTemplate() + "/trace.log"), body);
    }

}
