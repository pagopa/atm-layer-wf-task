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
        tags: { name: 'Menu'},
    };

    const body = mockBodyMenu();

    const responseMenu = http.post(`${baseUrl}${basePath}/${relativePath}`, body, params);

    console.log(`Menu call request duration: ${responseMenu.timings.duration} ms`);
    console.log('Request Menu:', responseMenu.request);
    console.log('Status Menu:', responseMenu.status);
    console.log('Body Menu:', responseMenu.body);

    check(responseMenu, {
        'response code was 201' : (res) => res.status == 201,
    })

    return responseMenu.body;
    
}