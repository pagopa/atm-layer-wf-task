import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, generateRandom13DigitNumber, checkError } from '../utils_function.js';

export function insertPaymentCode(baseUrl, basePath, token, spontaneousPayementResponse) {

    let responseParsed = JSON.parse(spontaneousPayementResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }


    const transactionId = JSON.parse(spontaneousPayementResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '03 Inserimento codice bollettino' },
    };

    const jsonData = JSON.parse(spontaneousPayementResponse).task;

    const scanPaymentRequestBody = {
        continue: true,
        result: "OK",
        codiceAvviso: "30205"+generateRandom13DigitNumber()
    };

    const body = mockedRequestBody(scanPaymentRequestBody, jsonData.id);

    let response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    //console.log(`insertPayementCode call request duration: ${response.timings.duration} ms`);

    //console.log('Request insert code Payement:', response.request);
    //console.log('Status insert code Payement:', response.status);
    //console.log('Body insert code Payement:', response.body);
    
    var count=0;
    while (response.status === 202 && count < 3) {
        //console.log('Retry insert Code:', count+1);
        response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    const hasError = checkError(response);

    let bodyResponse;
    if (hasError || count == 3) {
        let responseBodyObject = JSON.parse(response.body);
        responseBodyObject.status = 500;
        bodyResponse = JSON.stringify(responseBodyObject);
    } else {
        bodyResponse = response.body;
    }
    
    check(response, {
        'response code 03 Inserimento codice bollettino was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}