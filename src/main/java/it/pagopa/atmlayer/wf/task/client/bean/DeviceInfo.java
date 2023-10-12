package it.pagopa.atmlayer.wf.task.client.bean;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class DeviceInfo {

    public DeviceInfo(@NotNull(message = "BankID must not be null") String bankId,
            @NotNull(message = "BranchID must not be null") String branchId,
            @Pattern(regexp = "^[0-9]{1,4}$", message = "Device ID must match the regular expression") String code,
            @Pattern(regexp = "^[0-9a-zA-Z]{1,10}$", message = "Terminal ID must match the regular expression") String termId,
            Instant opTimestamp, DeviceType deviceType) {
        this.bankId = bankId;
        this.branchId = branchId;
        this.code = code;
        this.termId = termId;
        this.opTimestamp = opTimestamp;
        this.deviceType = deviceType;
    }

    @NotNull(message = "BankID must not be null")
    @Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008")
    @JsonProperty("bank_id")
    private String bankId;

    /*
     * Device branch ID.
     */
    @NotNull(message = "BranchID must not be null")
    @Schema(required = true, description = "Il codice identificativo della filiale (o codice CAB)", example = "12345")
    @JsonProperty("branch_id")
    private String branchId;

    /*
     * Device ID.
     */
    @Pattern(regexp = "^[0-9]{1,4}$", message = "Device ID must match the regular expression")
    @Schema(description = "Il codice identificativo dello sportello ATM (Codice Sportello o S.A. del Quadro Informativo. SPE-DEF-200)", example = "0001")
    private String code;

    /*
     * Terminal ID.
     */
    @Pattern(regexp = "^[0-9a-zA-Z]{1,10}$", message = "Terminal ID must match the regular expression")
    @Schema(description = "Il codice identificativo del dispositivo (o Terminal ID)", example = "ABCD1234")
    @JsonProperty("terminal_id")
    private String termId;

    /*
     * Terminal operation timestamp.
     */
    @Schema(description = "Timestamp della richiesta", format = "timestamp", pattern = "dd/MM/yyyy HH:mm")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @JsonProperty("op_timestamp")
    @JsonIgnore
    private Instant opTimestamp;

    /*
     * Type of device.
     */
    @Schema(description = "Identificativo del tipo di device")
    private DeviceType deviceType;

}
