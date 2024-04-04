package it.pagopa.atmlayer.wf.task.service.impl;

import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.service.S3ObjectStoreService;
import it.pagopa.atmlayer.wf.task.util.FileStorageS3Util;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.quarkus.scheduler.Scheduled;

@Singleton
@Slf4j
public class S3ObjectStoreServiceImpl implements S3ObjectStoreService {

    @Inject
    private FileStorageS3Util fileStorageS3Util;

    private Queue<String> messageBuffer = new ConcurrentLinkedQueue<>();

    public void writeLog(String message){
        log.info("Write log: " + message);
        messageBuffer.offer(message);
    }

    private void upload(String message){
        BlockingInputStreamAsyncRequestBody body = fileStorageS3Util.getBody();

        if (Objects.isNull(message)) {
            String errorMessage = String.format("message to save on S3: message %s non valido", message);
            log.error(errorMessage);
            throw new ErrorException(ErrorEnum.GENERIC_ERROR);
        }

        log.info("string to upload: {}: length={}", message, message.length());

        body.writeInputStream(new ByteArrayInputStream(message.getBytes()));
    }

    @Scheduled(every = "10s")
    public void executeHourlyTask() {
        fileStorageS3Util.createLogFile();
        while (!messageBuffer.isEmpty()) {
            String message = messageBuffer.poll(); 
            upload(message);
        }
    }

}