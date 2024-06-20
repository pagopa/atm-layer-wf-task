import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function getMessages(baseUrl, basePath, token, menuResponse) {

    let response = JSON.parse(menuResponse);

    if (response.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(menuResponse).transactionId;
    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '2 Recupera messaggi di cortesia e posizione debitoria' },
    };

    const jsonData = JSON.parse(menuResponse).task;

    const nextData = {
        "result": "OK"
    };

    const body = mockedRequestBody(nextData, jsonData.id);

    let responseMessages = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    let count = 0;
    while (responseMessages.status === 202 && count < 3) {
        responseMessages = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    const hasError = checkError(responseMessages);

    let bodyResponse;
    if (hasError || count == 3) {
        let responseBodyObject = JSON.parse(responseMessages.body);
        responseBodyObject.status = 500;
        bodyResponse = JSON.stringify(responseBodyObject);
    } else {
        bodyResponse = responseMessages.body;
    }

    check(responseMessages, {
        'response code get messages was 201': (res) => !hasError && res.status == 201,
    });

    //console.log("bodyResponse", bodyResponse);

    return bodyResponse;
}