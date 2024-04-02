package it.pagopa.atmlayer.wf.task.service.impl;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.client.bean.ObjectStorePutResponse;
import it.pagopa.atmlayer.wf.task.service.S3ObjectStoreService;
import it.pagopa.atmlayer.wf.task.util.FileStorageS3Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@ApplicationScoped
@Slf4j
public class S3ObjectStoreServiceImpl implements S3ObjectStoreService {

    @Inject
    S3AsyncClient s3;

    @Inject
    FileStorageS3Utils fileStorageS3Utils;


    public void writeLog(String message){
        if (Objects.isNull(message)) {
            String errorMessage = String.format("Aggiornamento file S3: message NULL %s non valido", message);
            log.error(errorMessage);
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        }
    
        PutObjectRequest putObjectRequest = fileStorageS3Utils.buildPutRequest();
        Uni.createFrom().future(() -> s3.putObject(putObjectRequest, AsyncRequestBody.fromString(message, StandardCharsets.UTF_8)))
            .onFailure().transform(error -> {
                String errorMessage = "Errore nel caricamento del file su S3";
                log.error(errorMessage, error);
                throw new ErrorException(ErrorEnum.GENERIC_ERROR);
            })
            .onItem().transformToUni(res -> {
                log.info("Success uploading from s3");
                return Uni.createFrom().item(ObjectStorePutResponse.builder().storageKey(putObjectRequest.key()).build());
            })
            .subscribe().with(
                response -> {}, // Empty subscriber as we don't need to do anything with the response
                throwable -> log.error("Error occurred while writing log", throwable)
            );
    }

}