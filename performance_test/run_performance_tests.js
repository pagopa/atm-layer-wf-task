import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { retrieveToken } from "./functions/token_retrieve.js";
import { nameThresholds, average_load, low_load } from "./options_settings.js";
import { menu } from "./functions/menu.js";
import { spontaneousPayment } from "./functions/spontaneous_payment.js";
import { exit } from "./functions/exit.js";
import { sleep } from "k6";
import { confirmExit } from "./functions/confirm_exit.js";
import { getMessages } from "./functions/get_messages.js";
import { selectSpontaneousPayment } from "./functions/select_spontaneous_payment.js";
import { insertPaymentCode } from "./functions/insert_payment_code.js";
import { insertFiscalcodeEC } from "./functions/insert_fiscalcode_ec.js";
import { reviewPaymentData } from "./functions/review_payment_data.js";
import { confirmPayment } from "./functions/confirm_payment.js";
import { authorizePayment } from "./functions/authorize_payment.js";

const baseUrl = `${__ENV.APPLICATION_BASE_URL}`;
const basePath = `${__ENV.APPLICATION_BASE_PATH}`;
const cognitoBaseUrl = `${__ENV.COGNITO_BASE_URL}`;
const cognitoClientId = `${__ENV.COGNITO_CLIENT_ID}`;
const cognitoClientSecret = `${__ENV.COGNITO_CLIENT_SECRET}`;

export const options = {
    thresholds: nameThresholds,
    scenarios: { average_load },
};

export function handleSummary(data) {
    return {
        "performance_summary.html": htmlReport(data),
    };
}

export default function () {
    const token = retrieveToken(cognitoBaseUrl, cognitoClientId, cognitoClientSecret);
    
    const menuResponse = menu(baseUrl, basePath, token);
    sleep(3);

    const messagesResponse = getMessages(baseUrl, basePath, token, menuResponse);
    sleep(3);

    const spontaneousPayementResponse = spontaneousPayment(baseUrl, basePath, token, messagesResponse);
    sleep(3);

    // Uncomment this line if you need to include paymentScan
    // const paymentScanResponse = payementScan(baseUrl, basePath, token, spontaneousPayementResponse);
    // sleep(3);

    const selectSpontaneousPaymentResponse = selectSpontaneousPayment(baseUrl, basePath, token, spontaneousPayementResponse);
    sleep(3);

    const payementCodeResponse = insertPaymentCode(baseUrl, basePath, token, selectSpontaneousPaymentResponse);
    sleep(3);

    const fiscalcodeECResponse = insertFiscalcodeEC(baseUrl, basePath, token, payementCodeResponse);
    sleep(3);

    const reviewPaymentDataResponse = reviewPaymentData(baseUrl, basePath, token, fiscalcodeECResponse);
    sleep(3);

    const confirmPaymentResponse = confirmPayment(baseUrl, basePath, token, reviewPaymentDataResponse);
    sleep(3);

    const authorizePaymentResponse = authorizePayment(baseUrl, basePath, token, confirmPaymentResponse);
    sleep(3);

    const exitResponse = exit(baseUrl, basePath, token, authorizePaymentResponse);
    confirmExit(baseUrl, basePath, token, exitResponse);
    sleep(3);
}
