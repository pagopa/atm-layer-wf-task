import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function getMessages(baseUrl, basePath, token, menuResponse){

    const transactionId = JSON.parse(menuResponse).transactionId;
    const relativePath = `next/trns/${transactionId}`;


    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Recupera messaggi di cortesia e posizione debitoria'},
    };

    const jsonData = JSON.parse(menuResponse).task;

    const nextData = {
        "result": "OK"
    };

    const body = mockedRequestBody(nextData, jsonData.id);

    const responseMessages = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    var count=0;
    while (responseMessages.status === 202 && count < 3) {
        responseMessages = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    check(responseMessages, {
        'response code was 201' : (res) => res.status === 201,
    })

    return responseMessages.body;
}