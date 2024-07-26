package it.pagopa.atmlayer.wf.task.bean;

import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.pagopa.atmlayer.wf.task.bean.enumartive.Channel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Informazioni del device")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Builder
public class Device {

	/*
	 * Device bank ID.
	 */
	@NotNull(message = "BankID non può essere null")
	@Schema(required = true, description = "Il codice identificativo della banca (o codice ABI)", example = "02008", format = "String", maxLength = 5)
	@Size(max = 5, message = "bankId is not valid")
	private String bankId;

	/*
	 * Device branch ID.
	 */
	@Schema( description = "Il codice identificativo della filiale (o codice CAB)", example = "12345", format = "String", maxLength = 5)
	@Size(max = 5, message = "branchId is not valid")
	private String branchId;

	/*
	 * Device ID.
	 */
	@Pattern(regexp = "^[0-9]{1,4}$", message = "Device code deve matchare l'espressione regolare")
	@Schema(description = "Il codice identificativo dello sportello ATM (Codice Sportello o S.A. del Quadro Informativo. SPE-DEF-200)", example = "0001", format = "String", maxLength = 4)
	@Size(max = 4, message = "code is not valid")
	private String code;

	/*
	 * Terminal ID.
	 */
	@Pattern(regexp = "^[0-9a-zA-Z]{1,10}$", message = "Terminal ID deve matchare l'espressione regolare\"")
	@Schema(description = "Il codice identificativo del dispositivo (o Terminal ID)", example = "ABCD1234", format = "String", maxLength = 100)
	@Size(max = 100, message = "terminalId is not valid")
	private String terminalId;

	/*
	 * Terminal operation timestamp.
	 */
	@Schema(description = "Timestamp della richiesta", implementation = Date.class, example="2018-03-20", maxLength = 23)
	private Date opTimestamp;

	/*
	 * Terminal channel.
	 */
	@Schema(description = "Identificativo del canale del dispositivo", enumeration = "[\"ATM\", \"KIOSK\"]", type = SchemaType.STRING)
	private Channel channel;

	@Schema(description = "Lista delle periferiche del device", type = SchemaType.ARRAY, maxItems = 10000)
	private List<Peripheral> peripherals;
}