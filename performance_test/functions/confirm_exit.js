import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function confirmExit(baseUrl, basePath, token, exitResponse) {

    const transactionId = JSON.parse(exitResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Close' },
    };

    const jsonData = JSON.parse(exitResponse).task;

    const body = mockedRequestBody({}, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Confirm Exit call request duration: ${response.timings.duration} ms`);
    console.log('Request Confirm Exit:', response.request);
    console.log('Status Confirm Exit:', response.status);
    console.log('Body Confirm Exit:', response.body);

    check(response, {
        'response code was 200': (response) => response.status === 200,
    })

    return response.body;
}