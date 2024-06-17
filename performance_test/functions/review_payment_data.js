import http from 'k6/http';
import { check } from 'k6';
import { mockedRequestBody, checkError } from '../utils_function.js';

export function reviewPaymentData(baseUrl, basePath, token, payementDataResponse) {

    let responseParsed = JSON.parse(payementDataResponse);

    if(responseParsed.status === 500) {
        const errorResponse = {
            status: 500
        }

        return errorResponse;
    }

    const transactionId = JSON.parse(payementDataResponse).transactionId;

    const relativePath = `next/trns/${transactionId}`;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: '05 Attivazione Bollettino' },
    };

    const jsonData = JSON.parse(payementDataResponse).task;

    const payementDataBody = {
        continue: true,
        goBack: false
    }

    const body = mockedRequestBody(payementDataBody, jsonData.id);

    let response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    //console.log(`reviewPaymentData call request duration: ${response.timings.duration} ms`);

    //console.log('Request review Payment:', response.request);
    //console.log('Status review Payment:', response.status);
    //console.log('Body review Payment:', response.body);
    
    var count=0;
    while (response.status === 202 && count < 3) {
        //console.log('Retry review Payment:', count+1);
        response = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    const hasError = checkError(response);
    
    let bodyResponse;
    if (hasError) {
        let responseBodyObject = JSON.parse(response.body);
        responseBodyObject.status = 500;
        bodyResponse = JSON.stringify(response);
    } else {
        bodyResponse = response.body;
    }

    check(response, {
        'response code 05 Attivazione Bollettino was 201': (res) => !hasError && res.status == 201,
    });

    return bodyResponse;
}