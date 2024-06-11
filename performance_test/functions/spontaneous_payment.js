import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function spontaneousPayment(baseUrl, basePath, token, messagesResponse){
    
    let response = JSON.parse(messagesResponse);

    if (response.status === 500) {
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

        const body = mockedRequestBody(paymentButton.data, jsonData.id);

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
            'response code spontaneous payment was 201': (res) => !hasError && res.status == 201,
        });

        return bodyResponse;
    }