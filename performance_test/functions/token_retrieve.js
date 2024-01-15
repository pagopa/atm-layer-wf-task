import http from 'k6/http';
import urlencode from 'https://jslib.k6.io/form-urlencoded/3.0.0/index.js';
import encoding from 'k6/encoding';

export function retrieveToken(cognitoBaseUrl, cognitoClientId, CognitoClientSecret) {

    const authParam = encoding.b64encode(`${cognitoClientId}:${CognitoClientSecret}`);
    const body = urlencode({ 'grant_type': 'client_credentials'});
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': `Basic ${authParam}`,
    };

    const params = {
        headers: headers,
        tags: { name: 'TokenRetrieve' }
    };

    const response = http.post(`${cognitoBaseUrl}`, body, params);

    console.log(`Token call request duration: ${response.timings.duration} ms`);
    console.log('Request Token:', response.request);
    console.log('Status Token:', response.status);
    console.log('Body Token:', response.body);

    console.log("ACCES TOKEN: ", JSON.parse(response.body).access_token)

    return JSON.parse(response.body).access_token;
}