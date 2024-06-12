import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function feeCalculation(baseUrl, basePath, token, reviewPaymentDataResponse) {

    let responseParsed = JSON.parse(reviewPaymentDataResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(reviewPaymentDataResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '06 Calcolo commissioni' },
    };

    const jsonData = JSON.parse(reviewPaymentDataResponse).task;

    const feeCalculationRequestBody = {
        continue: true,
        goBack: false
    }

    const body = mockedRequestBody(feeCalculationRequestBody, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Fee calculation call request duration: ${response.timings.duration} ms`);

    console.log('Request Fee calculation:', response.request);
    console.log('Status Fee calculation:', response.status);
    console.log('Body Fee calculation:', response.body);

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
        'response code Fee Calculation was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}