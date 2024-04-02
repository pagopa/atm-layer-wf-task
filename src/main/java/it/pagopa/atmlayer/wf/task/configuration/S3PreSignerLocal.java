package it.pagopa.atmlayer.wf.task.configuration;

import io.quarkus.arc.profile.UnlessBuildProfile;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.util.ObjectStoreProperties;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.net.URI;

@UnlessBuildProfile(anyOf = {"prod", "native"})
@Slf4j
public class S3PreSignerLocal {

    @Inject
    ObjectStoreProperties objectStoreProperties;

    private AwsCredentialsProvider getAwsCredentialProvider() {
        ObjectStoreProperties.Bucket bucketProps = objectStoreProperties.bucket();
        if (bucketProps.accessKey().isEmpty() || bucketProps.secretKey().isEmpty()) {
            log.error("Nessuna credenziale AWS fornita per la configurazione locale");
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        }
        AwsBasicCredentials awsBasicCredentials;
        awsBasicCredentials = AwsBasicCredentials.create(bucketProps.accessKey().get(), bucketProps.secretKey().get());
        return StaticCredentialsProvider.create(awsBasicCredentials);
    }

    @Singleton
    public S3AsyncClient s3AsyncClient() {
        log.info("Loading local AWS S3AsyncClient");
        ObjectStoreProperties.Bucket bucketProps = objectStoreProperties.bucket();
        if (bucketProps.endpointOverride().isEmpty()) {
            log.error("Nessun endpoint AWS fornito per la configurazione locale");
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        }
        return S3AsyncClient.builder()
                .region(Region.of(objectStoreProperties.bucket().region()))
                .credentialsProvider(getAwsCredentialProvider())
                .endpointOverride(URI.create(bucketProps.endpointOverride().get()))
                .build();
    }
}