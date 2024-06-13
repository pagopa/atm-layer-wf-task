import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function statusPayment(baseUrl, basePath, token, authorizePaymentResponse) {

    let responseParsed = JSON.parse(authorizePaymentResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(authorizePaymentResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '09 Ricezione stato pagamento' },
    };

    const jsonData = JSON.parse(authorizePaymentResponse).task;

    const statusPaymentRequestBody = {
        result: "OK"
    }

    const body = mockedRequestBody(statusPaymentRequestBody, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Status payment call request duration: ${response.timings.duration} ms`);

    console.log('Request Status payment:', response.request);
    console.log('Status Status payment:', response.status);
    console.log('Body Status payment:', response.body);

    var count=0;
    while (response.status === 202 && count < 3) {
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
        'response code 09 Ricezione stato pagamento was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}