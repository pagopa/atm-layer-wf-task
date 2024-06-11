import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function insertFiscalcodeEC(baseUrl, basePath, token, spontaneousPayementResponse) {

    let responseParsed = JSON.parse(spontaneousPayementResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(spontaneousPayementResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Inserimento codice fiscale ente creditore' },
    };

    const jsonData = JSON.parse(spontaneousPayementResponse).task;

    const scanPaymentRequestBody = {
        continue: true,
        result: "OK",
        codiceEnte:  "00000000201"
    }

    const body = mockedRequestBody(scanPaymentRequestBody, jsonData.id);

    let response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`insertFiscalcodeEC call request duration: ${response.timings.duration} ms`);

    console.log('Request insert EC Payement:', response.request);
    console.log('Status insert EC Payement:', response.status);
    console.log('Body insert EC Payement:', response.body);
    
    var count=0;
    while (response.status === 202 && count < 3) {
        console.log('Retry insert EC:', count+1);
        response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    const hasError = checkError(response);

    let bodyResponse;
    if (hasError) {
        let responseBodyObject = JSON.parse(response.body);
        responseBodyObject.status = 500;
        bodyResponse = JSON.stringify(responseBodyObject);
    } else {
        bodyResponse = response.body;
    }
    
    check(response, {
        'response code insert fiscalcode EC was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}