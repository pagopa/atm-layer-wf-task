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
    
                    "status": "OK"
    
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
    
                    "status": "OK"
    
                }
    
            ],
    
            "terminalId": terminalId
    
        },
    
        "taskId": taskId
    
    }

    return JSON.stringify(mock);
}