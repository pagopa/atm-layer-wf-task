import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody } from '../utils_function.js';

export function spontaneousPayment(baseUrl, basePath, token, menuResponse){

    const transactionId = JSON.parse(menuResponse).transactionId;
    const relativePath = `next/trns/${transactionId}`;


    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Seleziona pagamento spontaneo'},
    };

    const jsonData = JSON.parse(menuResponse).task;

    const paymentNotice = jsonData.buttons.find((e) => {
        if (e.id === "pagamentoAvviso") {
            return e.data;
        };
    });

    const body = mockedRequestBody(paymentNotice.data, jsonData.id);

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