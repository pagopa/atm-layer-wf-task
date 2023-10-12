package it.pagopa.atmlayer.wf.task.bean;

import java.time.Instant;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "The Device infos.")
@JsonInclude(Include.NON_NULL)
public class Device {
	/*
	 * Device bank ID.
	 */
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
	//@Schema(description = "Timestamp della richiesta", implementation = String.class, format = "timestamp", pattern = "dd/MM/yyyy HH:mm")
	//@Schema(description = "Timestamp della richiesta", pattern = "dd/MM/yyyy HH:mm")
	//@JsonFormat(pattern = "dd-MM-yyyy HH:mm")
	@JsonProperty("op_timestamp")
	@JsonIgnore
	private Instant opTimestamp;

	/*
	 * Terminal channel.
	 */
	@Schema(description = "Identificativo del canale del dispositivo")
	private Channel channel;
	
	private List<Peripheral> peripherals;
}