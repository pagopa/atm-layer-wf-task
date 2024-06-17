import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function exit(baseUrl, basePath, token, scanPaymentResponse) {

    let responseParsed = JSON.parse(scanPaymentResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(scanPaymentResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '10 Seleziona uscita' },
    };

    const jsonData = JSON.parse(scanPaymentResponse).task;

    const exitRequestBody = {
        continue: false,
    }

    const body = mockedRequestBody(exitRequestBody, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    //console.log(`Exit call request duration: ${response.timings.duration} ms`);

    //console.log('Request Exit:', response.request);
    //console.log('Status Exit:', response.status);
    //console.log('Body Exit:', response.body);

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
        'response code 10 Seleziona uscita was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}