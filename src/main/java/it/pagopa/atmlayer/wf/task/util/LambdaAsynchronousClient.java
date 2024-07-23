package it.pagopa.atmlayer.wf.task.util;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.LambdaException;

/**
 * This class represents an entry point to lambda functions defined into AWS. It
 * permits to invoke asynchronous a specified lambda function passing a payload.
 * 
 * @author pasquales
 *
 */
@Slf4j
@Singleton
public class LambdaAsynchronousClient {

	@Inject
	private LambdaAsyncClient lambdaAsyncClient;

	private void invokeAsync(SdkBytes payload, String functionName) {
		try {
			lambdaAsyncClient.invoke(InvokeRequest.builder().payload(payload).invocationType(InvocationType.EVENT)
					.functionName(functionName).build());
		} catch (SdkException e) {
			log.error("Error calling latency-logging lambda:", e);
		}
	}

	private static SdkBytes buildPayload(Object request) {
		return SdkBytes.fromByteArray(Utility.getJson(request).getBytes());
	}

	public void executeLambda(Object request, String functionName) {
		invokeAsync(buildPayload(request), functionName);
	}

}
