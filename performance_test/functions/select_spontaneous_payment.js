import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function selectSpontaneousPayment(baseUrl, basePath, token, messagesResponse){

    let response = JSON.parse(messagesResponse);

    if(response.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse; 
    }

    const transactionId = JSON.parse(messagesResponse).transactionId;
    const relativePath = `next/trns/${transactionId}`;

    let jsonData = JSON.parse(messagesResponse).task;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '4 Seleziona inserisci dati bollettino'},
    };

    let paymentButton = jsonData.buttons.find(button => button.id === "pagamentoAvviso");

    const body = mockedRequestBody((paymentButton.data).data, jsonData.id);

    const responseSpsPayment = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Select Spontaneous Payment call request duration: ${responseSpsPayment.timings.duration} ms`);
    console.log('Request Select Spontaneous Payment:', responseSpsPayment.request);
    console.log('Status Select Spontaneous Payment:', responseSpsPayment.status);
    console.log('Body Select Spontaneous Payment:', responseSpsPayment.body);

    var count=0;
    while (responseSpsPayment.status === 202 && count < 3) {
        responseSpsPayment = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    const hasError = checkError(responseSpsPayment);

    let bodyResponse;
    if (hasError) {
        let responseBodyObject = JSON.parse(responseSpsPayment.body);
        responseBodyObject.status = 500;
        bodyResponse = JSON.stringify(responseBodyObject);
    } else {
        bodyResponse = responseSpsPayment.body;
    }
    
    check(responseSpsPayment, {
        'response code select spontaneous payment was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}