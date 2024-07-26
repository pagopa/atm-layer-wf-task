package it.pagopa.atmlayer.wf.task.client.lambda;

import it.pagopa.atmlayer.wf.task.util.LambdaAsynchronousClient;
import jakarta.inject.Singleton;

/**
 * Calls the lambda latency-logging function to log the latency of the transaction into Cloudwatch.
 * 
 * @author pasquales
 *
 */
@Singleton
public class LatencyLoggingLambdaClient extends LambdaAsynchronousClient {

	private static final String EXTERNAL = "External";

	private static final String INTERNAL = "Internal";

	public static final String LATENCY_LOGGING_LAMBDA_FUNCTION_NAME = "latency-logging";

	public void log(long start, long stop, Boolean externalComm) {
		executeLambda(LatencyLoggingRequest.builder()
				.latencyType(externalComm.equals(Boolean.valueOf(true)) ? EXTERNAL : INTERNAL)
				.latencyValue(String.valueOf(stop - start)).build(), LATENCY_LOGGING_LAMBDA_FUNCTION_NAME);
	}

}
