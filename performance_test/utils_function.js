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
    
            "terminalId": "64874412"
    
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
    
            "terminalId": "64874412"
    
        },
    
        "taskId": taskId
    
    }

    return JSON.stringify(mock);
}