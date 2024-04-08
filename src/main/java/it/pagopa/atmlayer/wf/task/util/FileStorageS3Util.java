package it.pagopa.atmlayer.wf.task.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Getter
@Singleton
public class FileStorageS3Util {

    @Inject
    Properties properties;

    BlockingInputStreamAsyncRequestBody body;

    @Inject
    S3Client s3;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    /* @PostConstruct
    public void init() {
        s3 = S3Client.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(properties.bucket().region()))
            .build();
    } */

    public void createLogFile(String message){
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(formatter);
        
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.bucket().name())
                .key(properties.resource().pathTemplate().concat("/trace-").concat(System.getenv("POD_NAME")).concat("-").concat(formattedDateTime).concat(".log"))
                .build();

        s3.putObject(request, RequestBody.fromString(message));
    }

}
