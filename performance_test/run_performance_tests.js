import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { retrieveToken } from "./functions/token_retrieve.js";
import { nameThresholds, average_load } from "./options_settings.js";
import { menu } from "./functions/menu.js";
import { spontaneousPayment } from "./functions/spontaneous_payement.js";
import { payementScan } from "./functions/payement_scan.js";
import { exit } from "./functions/exit.js";
import { sleep } from "k6";
import { confirmExit } from "./functions/confirm_exit.js";

const baseUrl = `${__ENV.APPLICATION_BASE_URL}`;
const basePath = `${__ENV.APPLICATION_BASE_PATH}`;
const cognitoBaseUrl = `${__ENV.COGNITO_BASE_URL}`;
const cognitoClientId = `${__ENV.COGNITO_CLIENT_ID}`;
const cognitoClientSecret = `${__ENV.COGNITO_CLIENT_SECRET}`;

export const options = {
    thresholds: nameThresholds,
    scenarios: { average_load },
}

export function handleSummary(data) {
    return {
        "performance_summary.html": htmlReport(data),
    };
}

export default function () {
    const token = retrieveToken(cognitoBaseUrl, cognitoClientId, cognitoClientSecret);
    const menuRespose = menu(baseUrl,basePath, token);
    sleep(3);
    const spontaneousPayementResponse = spontaneousPayment(baseUrl, basePath, token, menuRespose);
    sleep(3);
    const paymentScanResponse = payementScan(baseUrl, basePath, token, spontaneousPayementResponse);
    sleep(3);
    const exitResponse = exit(baseUrl, basePath, token, paymentScanResponse);
    confirmExit(baseUrl, basePath, token, exitResponse);
    sleep(3);
}
