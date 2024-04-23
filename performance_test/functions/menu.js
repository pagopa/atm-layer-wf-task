import http from 'k6/http';
import { group, check } from 'k6';
import { mockBodyMenu } from '../utils_function.js';

export function menu(baseUrl, basePath, token){

    const relativePath = 'main';


    const headers = {
        'Content-Type': 'application/json',
        'Authorization': token,
    };

    const params = {
        headers: headers,
        tags: { name: 'Menu iniziale'},
    };

    const body = mockBodyMenu();

    const responseMenu = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Menu call request duration: ${responseMenu.timings.duration} ms`);
    console.log('Request Menu:', responseMenu.request);
    console.log('Status Menu:', responseMenu.status);
    console.log('Body Menu:', responseMenu.body);

    var count=0;
    while (responseMenu.status === 202 && count < 1) {
        responseMenu = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);
        count++;
    }

    check(responseMenu, {
        'response code was 201' : (res) => res.status == 201,
    })

    return responseMenu.body;
    
}