import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function confirmExit(baseUrl, basePath, token, exitResponse) {

    let responseParsed = JSON.parse(exitResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(exitResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Conferma uscita' },
    };

    const jsonData = JSON.parse(exitResponse).task;

    const confirmExitDataBody = { 
        result: "OK"
    }

    const body = mockedRequestBody({confirmExitDataBody}, jsonData.id);

    const response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Confirm Exit call request duration: ${response.timings.duration} ms`);
    console.log('Request Confirm Exit:', response.request);
    console.log('Status Confirm Exit:', response.status);
    console.log('Body Confirm Exit:', response.body);

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
        'response code confirm exit was 200': (res) => !hasError && res.status == 200,
    });

    return bodyResponse;
}