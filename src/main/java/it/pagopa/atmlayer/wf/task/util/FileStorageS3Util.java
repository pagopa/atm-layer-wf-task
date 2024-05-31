package it.pagopa.atmlayer.wf.task.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.quarkus.arc.profile.UnlessBuildProfile;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Getter
@Singleton
@UnlessBuildProfile(anyOf = { "native" })
public class FileStorageS3Util {

    @Inject
    Properties properties;

    BlockingInputStreamAsyncRequestBody body;

    S3Client s3;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
    
    @PostConstruct
    public void init() {
        s3 = S3Client.builder()
                .httpClient(UrlConnectionHttpClient.create())
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(properties.bucket().region()))
                .build();
    }

    public void createLogFile(String message) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(formatter);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.bucket().name())
                .key(properties.resource().pathTemplate().concat("/").concat(formattedDateTime.substring(0, 10))
                        .concat("/").concat(formattedDateTime.substring(11, 13)).concat("/")
                        .concat(System.getenv("POD_NAME")).concat("/trace-").concat(formattedDateTime).concat(".log"))
                .contentType("binary/octet-stream")
                .build();

        s3.putObject(request, RequestBody.fromString(message));
    }

}
