package it.pagopa.atmlayer.wf.task.util;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.internal.TransferManagerFactory;
import software.amazon.awssdk.transfer.s3.internal.TransferManagerFactory.DefaultBuilder;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

@Getter
@Singleton
public class FileStorageS3Util {

    @Inject
    Properties properties;

    S3AsyncClient s3Client;

    
    @PostConstruct
    public void init() {
        s3Client = S3AsyncClient.crtBuilder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(properties.bucket().region()))
            .build();
    }

    public void appendS3(String message) {
        S3TransferManager tm = TransferManagerFactory.createTransferManager(new DefaultBuilder().s3Client(s3Client));
        UploadRequest uploadRequest = UploadRequest.builder()
                                                        .requestBody(AsyncRequestBody.fromString(message))
                                                        .putObjectRequest(req -> req.bucket(properties.bucket().name()).key(properties.resource().pathTemplate() + "/trace.log"))
                                                        .build();

        tm.upload(uploadRequest);
    }

}
