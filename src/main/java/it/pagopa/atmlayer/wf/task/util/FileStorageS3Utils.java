package it.pagopa.atmlayer.wf.task.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ApplicationScoped
public class FileStorageS3Utils {
    
    @Inject
    ObjectStoreProperties objectStoreProperties;

    public PutObjectRequest buildPutRequest() {
        return PutObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(objectStoreProperties.resource().pathTemplate().concat("/").concat("trace.log"))
                .build();
    }

    public GetObjectRequest buildGetRequest(String key) {
        return GetObjectRequest.builder()
                .bucket(objectStoreProperties.bucket().name())
                .key(key)
                .build();
    }
}
