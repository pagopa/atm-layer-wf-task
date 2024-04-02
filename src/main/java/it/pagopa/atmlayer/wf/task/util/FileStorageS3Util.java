package it.pagopa.atmlayer.wf.task.util;

import java.util.concurrent.CompletableFuture;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Getter
@Singleton
public class FileStorageS3Util {

    @Inject
    S3AsyncClient s3;

    @Inject
    ObjectStoreProperties objectStoreProperties;

    BlockingInputStreamAsyncRequestBody body;

    CompletableFuture<PutObjectResponse> responseFuture;

    public FileStorageS3Util() {
        body = AsyncRequestBody.forBlockingInputStream(null);

        responseFuture = s3.putObject(r -> r.bucket(objectStoreProperties.bucket().name())
                .key(objectStoreProperties.resource().pathTemplate() + "/trace.log"), body);
    }
}
