package it.pagopa.atmlayer.wf.task.client.bean.enumerative;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequiredVariables {
    ACQUIRER_ID("AcquirerId"),
    BRANCH_ID("BranchId"),
    TERMINAL_ID("TerminalId"),
    TRANSACTION_ID("TransactionId"),
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    GRANT_TYPE("grant_type"),
    CONTENT_TYPE("content_type"),
    FISCAL_CODE("fiscal_code");

    private final String value;
}
