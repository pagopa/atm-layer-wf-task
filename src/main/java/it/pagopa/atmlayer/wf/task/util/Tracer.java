package it.pagopa.atmlayer.wf.task.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.inject.Inject;

@RegisterForReflection
public class Tracer {

    @Inject
    private Properties properties;

    @Inject
    private S3ObjectStoreServiceImpl objectStoreServiceImpl;

    private static StringBuilder messageBuilder = new StringBuilder();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void trace(String transactionId, String toLog) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(formatter).concat(" | ");
        messageBuilder.append(formattedDateTime).append(" ").append(transactionId).append(" | ").append(toLog)
                .append("\n");
    }

    @Scheduled(every = "2m", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob() {
        if (properties.isTraceLoggingEnabled() && messageBuilder.length() > 0) {
            objectStoreServiceImpl.writeLog(messageBuilder.toString().replaceAll("\\{\\}", ""));
            messageBuilder.setLength(0);
        }
    }

    public static void traceMilAuthClientComm(State state, Device device,
            RestResponse<TokenResponse> restTokenResponse) {
        String transactionId = state.getTransactionId();
        trace(transactionId, " ============== REQUEST MIL AUTH CLIENT ==============");
        trace(transactionId, " HEADERS: AcquirerId: "+ device.getBankId() + " Channel: " + device.getChannel().name() + " FiscalCode: "+  state.getFiscalCode() + " TerminalId:" + device.getTerminalId() + " TransactionId"+state.getTransactionId());
        trace(transactionId, " METHOD: POST");
        trace(transactionId, " ============== REQUEST MIL AUTH CLIENT ==============");
        trace(transactionId, " ============== RESPONSE MIL AUTH CLIENT ==============");
        if (restTokenResponse != null) {
            trace(transactionId, " STATUS: ".concat(String.valueOf(restTokenResponse.getStatus())));
            if (!Objects.isNull(restTokenResponse.getEntity())){
                trace(transactionId, " BODY: Access token:" + restTokenResponse.getEntity().getAccess_token());
            }
        } else {
            trace(transactionId, " Error while communicating with MilAuth!");
        }
        trace(transactionId, " ============== RESPONSE MIL AUTH CLIENT ==============");
    }

}
