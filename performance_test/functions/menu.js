import http from 'k6/http';
import { group, check } from 'k6';
import { mockBodyMenu, checkError } from '../utils_function.js';

export function menu(baseUrl, basePath, token){

    const relativePath = 'main';


        const headers = {
            'Content-Type': 'application/json',
            'Authorization': token,
        };

        const params = {
            headers: headers,
            tags: { name: '01 Menu iniziale'},
        };

        const body = mockBodyMenu();

        let responseBodyObject = JSON.parse(body);
        responseBodyObject.fiscalcode = "SNNCNA88S04A567U";
        const bodyWithFC = JSON.stringify(responseBodyObject);

    if(token) {

        const responseMenu = http.post(`${baseUrl}${basePath}/${relativePath}`, bodyWithFC, params);

        console.log(`Menu call request duration: ${responseMenu.timings.duration} ms`);
        console.log('Request Menu:', responseMenu.request);
        console.log('Status Menu:', responseMenu.status);
        console.log('Body Menu:', responseMenu.body);

        var count=0;
        while (responseMenu.status === 202 && count < 3) {
            responseMenu = http.post(`${baseUrl}${basePath}/${relativePath}`, bodyWithFC, params);
            count++;
        }

        const hasError = checkError(responseMenu);

        check(responseMenu, {
            'response code 01 Menu iniziale was 201': (res) => !hasError && res.status == 201,
        });

        return responseMenu.body;
    }

    const errorResponse = {
        status: 500
    }

    return errorResponse;
    
}