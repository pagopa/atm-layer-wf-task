import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function spontaneousPayment(baseUrl, basePath, token, messagesResponse){

    const transactionId = JSON.parse(messagesResponse).transactionId;
    const relativePath = `next/trns/${transactionId}`;

    let jsonData = JSON.parse(messagesResponse).task;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const noTagParams = {
        headers: headers
    };
    const confirmData = {
        "continue": "true"
    };
    let confirmBody = mockedRequestBody(confirmData, jsonData.id);

    const params = {
        headers: headers,
        tags: { name: 'Seleziona pagamento spontaneo'},
    };

    let paymentButton = jsonData.buttons.find(button => button.id === "pagamentoAvviso");
    
    while (paymentButton === undefined) {
        jsonData = JSON.parse(http.post(`${baseUrl}${basePath}/${relativePath}`, confirmBody, noTagParams).body).task;
        paymentButton = jsonData.buttons.find(button => button.id === "pagamentoAvviso");
        confirmBody = mockedRequestBody(confirmData, jsonData.id);
    }

    const body = mockedRequestBody((paymentButton.data).data, jsonData.id);

    const responseSpsPayment = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Spontaneous Payement call request duration: ${responseSpsPayment.timings.duration} ms`);
    console.log('Request Spontaneous Payement:', responseSpsPayment.request);
    console.log('Status Spontaneous Payement:', responseSpsPayment.status);
    console.log('Body Spontaneous Payement:', responseSpsPayment.body);

    var count=0;
    while (responseSpsPayment.status === 202 && count < 3) {
        responseSpsPayment = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    check(responseSpsPayment, {
        'response code was 201' : (res) => res.status === 201,
    })

    return responseSpsPayment.body;
}