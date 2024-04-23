package it.pagopa.atmlayer.wf.task.service.impl;

import it.pagopa.atmlayer.wf.task.service.S3ObjectStoreService;
import it.pagopa.atmlayer.wf.task.util.FileStorageS3Util;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Singleton implementation of the {@link S3ObjectStoreService} interface.
 */
@Singleton
public class S3ObjectStoreServiceImpl implements S3ObjectStoreService {
    
    @Inject
    private FileStorageS3Util fileStorageS3Util;

    /**
     * Writes a log message to Amazon S3.
     * 
     * @param message The log message to write
     */
    public void writeLog(String message) {
        fileStorageS3Util.createLogFile(message);
    }

}