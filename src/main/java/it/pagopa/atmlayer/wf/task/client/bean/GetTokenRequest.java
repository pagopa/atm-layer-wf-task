package it.pagopa.atmlayer.wf.task.client.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@RegisterForReflection
@JsonInclude(Include.NON_NULL)
public class GetTokenRequest {
    
    String kid;

    byte[] encryptedPan;
    
}
