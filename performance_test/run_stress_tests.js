import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { retrieveToken } from "./functions/token_retrieve.js";
import { nameThresholds, max_rate } from "./options_settings.js";
import { menu } from "./functions/menu.js";
import { exit } from "./functions/exit.js";
import { sleep } from "k6";
import { confirmExit } from "./functions/confirm_exit.js";
import { insertPaymentCode } from "./functions/insert_payment_code.js";
import { insertFiscalcodeEC } from "./functions/insert_fiscalcode_ec.js";
import { reviewPaymentData } from "./functions/review_payment_data.js";
import { confirmPayment } from "./functions/confirm_payment.js";
import { authorizePayment } from "./functions/authorize_payment.js";
import {spontaneousPayment} from "./functions/spontaneous_payment.js"
import { feeCalculation } from "./functions/fee_calculation.js";
import { statusPayment } from "./functions/status_payment.js";
import { idPaySelect } from "./functions/Id_pay.js";

const baseUrl = `${__ENV.APPLICATION_BASE_URL}`;
const basePath = `${__ENV.APPLICATION_BASE_PATH}`;
const cognitoBaseUrl = `${__ENV.COGNITO_BASE_URL}`;
const cognitoClientId = `${__ENV.COGNITO_CLIENT_ID}`;
const cognitoClientSecret = `${__ENV.COGNITO_CLIENT_SECRET}`;

export const options = {
    thresholds: nameThresholds,
    scenarios: { max_rate },
};

export function handleSummary(data) {
    return {
        "stress_summary.html": htmlReport(data),
    };
}

export default function () {
    const token = retrieveToken(cognitoBaseUrl, cognitoClientId, cognitoClientSecret);
    
    const menuResponse = menu(baseUrl, basePath, token);
    

    // Predisposizione flusso IdPay
    // const idPaySelectResponse = idPaySelect(baseUrl, basePath, token, menuResponse);

//    const spontaneousPayementResponse = spontaneousPayment(baseUrl, basePath, token, menuResponse);
//
//
//    const payementCodeResponse = insertPaymentCode(baseUrl, basePath, token, spontaneousPayementResponse);
//
//
//    const fiscalcodeECResponse = insertFiscalcodeEC(baseUrl, basePath, token, payementCodeResponse);
//
//
//    const reviewPaymentDataResponse = reviewPaymentData(baseUrl, basePath, token, fiscalcodeECResponse);
//
//
//    const feeCalculationResponse = feeCalculation(baseUrl, basePath, token, reviewPaymentDataResponse);
//
//
//    const confirmPaymentResponse = confirmPayment(baseUrl, basePath, token, feeCalculationResponse);
//
//
//    const authorizePaymentResponse = authorizePayment(baseUrl, basePath, token, confirmPaymentResponse);
//
//
//    const statusPaymentResponse = statusPayment(baseUrl, basePath, token, authorizePaymentResponse);
//
//
//    const exitResponse = exit(baseUrl, basePath, token, statusPaymentResponse);
//    confirmExit(baseUrl, basePath, token, exitResponse);

}
