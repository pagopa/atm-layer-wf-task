import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function payementScan(baseUrl, basePath, token, spontaneousPayementResponse) {

    const transactionId = JSON.parse(spontaneousPayementResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Inserimento codice bollettino' },
    };

    const jsonData = JSON.parse(spontaneousPayementResponse).task;

    const scanPaymentRequestBody = {
        continue: true,
        result: "OK",
        scanData: "UEFHT1BBfDAwMnwzMDIwNTEyMzQ1NjcwMDAwNzB8Nzc3Nzc3Nzc3Nzd8MTAwMDA"
    }

    const body = mockedRequestBody(scanPaymentRequestBody, jsonData.id);

    let response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`PaymentScan call request duration: ${response.timings.duration} ms`);

    console.log('Request Scan Payement:', response.request);
    console.log('Status Scan Payement:', response.status);
    console.log('Body Scan Payement:', response.body);
    
    var count=0;
    while (response.status === 202 && count < 3) {
        console.log('Retry Scan Payement:', count+1);
        response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    check(response, {
        'response code was 201': (response) => response.status === 201,
    })

    return response.body;
}