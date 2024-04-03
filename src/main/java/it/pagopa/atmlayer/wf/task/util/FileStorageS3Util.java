package it.pagopa.atmlayer.wf.task.util;

import java.util.List;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;

@Getter
@Singleton
public class FileStorageS3Util {

    @Inject
    Properties properties;

    AmazonS3 s3Client;

    InitiateMultipartUploadRequest initRequest;

    @PostConstruct
    public void init() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(properties.bucket().region())
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    public InitiateMultipartUploadResult initMultipartUpload() {
        initRequest = new InitiateMultipartUploadRequest(properties.bucket().name(), properties.resource().pathTemplate().concat("/trace.log"));
        return s3Client.initiateMultipartUpload(initRequest);
    }

    public UploadPartResult uploadPart(UploadPartRequest uploadRequest){
        // Upload the part
        return s3Client.uploadPart(uploadRequest);
    }

    public void completeUpload(InitiateMultipartUploadResult initResponse, List<PartETag> partETags){
        // Complete the multipart upload.
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(properties.bucket().name(), properties.resource().pathTemplate().concat("/trace.log"), initResponse.getUploadId(), partETags);
        s3Client.completeMultipartUpload(compRequest);
    }

}
