import { b64decode } from 'k6/encoding';

const terminalId = Math.floor(10000000 + Math.random() * 90000000);

export function mockBodyMenu () {
    const mock = {

        "data": {
    
            "continue": true
    
        },
    
        "device": {
    
            "bankId": "06789",
    
            "branchId": "12345",
    
            "channel": "ATM",
    
            "code": "0001",
    
            "opTimestamp": "2023-10-31T16:30:00",
    
            "peripherals": [
    
                {
    
                    "id": "PRINTER",
    
                    "name": "Receipt printer",
    
                    "status": "OK"
    
                },
    
                {
    
                    "id": "SCANNER",
    
                    "name": "Scanner",
    
                    "status": "KO"
    
                }
    
            ],
    
            "terminalId": terminalId
    
        }
    
    }

    return JSON.stringify(mock);
}

export function mockedRequestBody (paymentNotice, taskId) {
    const mock = {

        "data": paymentNotice,
    
        "device": {
    
            "bankId": "06789",
    
            "branchId": "12345",
    
            "channel": "ATM",
    
            "code": "0001",
    
            "opTimestamp": "2023-10-31T16:30:00",
    
            "peripherals": [
    
                {
    
                    "id": "PRINTER",
    
                    "name": "Receipt printer",
    
                    "status": "OK"
    
                },
    
                {
    
                    "id": "SCANNER",
    
                    "name": "Scanner",
    
                    "status": "KO"
    
                }
    
            ],
    
            "terminalId": terminalId
    
        },
    
        "taskId": taskId
    
    }

    return JSON.stringify(mock);
}

export function generateRandom13DigitNumber() {
    const random13DigitNumber = Math.floor(Math.random() * Math.pow(10, 13));

    return random13DigitNumber.toString().padStart(13, '0');
}

export function checkError(response) {
    let jsonBody;
    try {
        jsonBody = JSON.parse(response.body);
    } catch (e) {
        return;
    }

    if (jsonBody.task && jsonBody.task.template) {
        if (jsonBody.task.template.type === "INFO") {
            const decodedContent = b64decode(jsonBody.task.template.content, "std", "s");
            return decodedContent.toLowerCase().includes('errore');
        } else {
            return false;
        }
    } else {
        return false;
    }
}