package it.pagopa.atmlayer.wf.task.bean.enumartive;

public enum Command {

	AUTHORIZE, // Fase di autorizzazione dell'importo del bollettino (transazione di monetica)
	PRINT_RECEIPT, // Stampa dello scontrino
	SCAN_BIIL_DATA, // Scansione QR code o Data matrix (o Bar code)
	SCAN_FISCAL_CODE, // Scansione Codice Fiscale
	END, // Fine della transazione
	GET_IBAN, // Recupero iban
	GET_PAN, // Recupero PAN
	NEXT, // Richiesta next step della transazione
	AUTHENTICATION, // Fase di autenticazione dell'utente
	GET_CF // Recupero codice fiscale
	
}
