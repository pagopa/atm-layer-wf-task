package it.pagopa.atmlayer.wf.task.bean.Exceptions;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({ "errorCode", "description", "status" })
@RegisterForReflection
public class ErrorResponse {

    private int status;

    private String errorCode;

    private String description;

}
