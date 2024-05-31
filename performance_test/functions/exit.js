import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function exit(baseUrl, basePath, token, scanPaymentResponse) {

    const transactionId = JSON.parse(scanPaymentResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Seleziona uscita' },
    };

    const jsonData = JSON.parse(scanPaymentResponse).task;

    const exitRequestBody = {
        continue: false,
    }

    const body = mockedRequestBody(exitRequestBody, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Exit call request duration: ${response.timings.duration} ms`);

    console.log('Request Exit:', response.request);
    console.log('Status Exit:', response.status);
    console.log('Body Exit:', response.body);

    var count=0;
    while (response.status === 202 && count < 3) {
        response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    check(response, {
        'response code was 201': (response) => response.status === 201,
    })

    return response.body;
}