package it.pagopa.atmlayer.wf.task.service.impl;

import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.service.S3ObjectStoreService;
import it.pagopa.atmlayer.wf.task.util.FileStorageS3Util;
import it.pagopa.atmlayer.wf.task.util.Properties;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;

@Singleton
@Slf4j
public class S3ObjectStoreServiceImpl implements S3ObjectStoreService {

    @Inject
    private FileStorageS3Util fileStorageS3Util;

    @Inject
    private Properties properties;
    
    /* Create a list of ETag objects. You retrieve ETags for each object part
    uploaded, then, after each individual part has been uploaded, pass the list of ETags to
    the request to complete the upload. */
    private List<PartETag> partETags  = new ArrayList<PartETag>();

    private InitiateMultipartUploadResult initResponse;

    private int partNumber = 0;
    
    public void writeLog(String message){
        try {

            initResponse = fileStorageS3Util.getInitResponse();

            log.debug("initMultipartUpload processed");

            // Create the request to upload a part.
            UploadPartRequest uploadRequest = new UploadPartRequest()
            .withBucketName(properties.bucket().name())
            .withKey(properties.resource().pathTemplate().concat("/trace.log"))
            .withUploadId(initResponse.getUploadId())
            .withInputStream(new ByteArrayInputStream(message.getBytes()))
            .withPartNumber(++partNumber);
            
            
            UploadPartResult uploadResult = fileStorageS3Util.uploadPart(uploadRequest);
            log.debug("uploadRequest sent!");

            partETags.add(uploadResult.getPartETag());

            if (partNumber ==10) {
                fileStorageS3Util.completeUpload(initResponse, partETags);
                log.info("Upload on S3 finished!");
            }
        } catch (AmazonServiceException e) {
            log.error("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.", e);
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        } catch (SdkClientException e) {
            log.error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.", e);
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        }
    }
    
}