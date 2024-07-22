package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Date;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class DeviceInfo {

    @NotNull(message = "BankID must not be null")
    @Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008")
    private String bankId;

    /*
     * Device branch ID.
     */
    @NotNull(message = "BranchID must not be null")
    @Schema(required = true, description = "Il codice identificativo della filiale (o codice CAB)", example = "12345")
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
    private String terminalId;

    /*
     * Terminal operation timestamp.
     */
    @Schema(description = "Timestamp della richiesta", format = "timestamp", pattern = "yyyy-MM-ddTHH:mm:ss", example = "2023-10-31T17:30:00")
    private Date opTimestamp;

    /*
     * Type of device.
     */
    @Schema(description = "Identificativo del tipo di device", implementation = DeviceType.class, enumeration = "[\"ATM\", \"KIOSK\"]", type = SchemaType.STRING, format = "string")
    private DeviceType channel;

}
