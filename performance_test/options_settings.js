export const thresholdsSettings = {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
    http_req_duration: ['p(99)<2000'],
  };
  
  export const nameThresholds={
    //MENU
    'http_req_duration{name:Menu}': ['p(95)<15000'],
    'http_req_waiting{name:Menu}':['p(95)<15000'],
    'http_req_failed{name:Menu}':['rate<0.01'],
    'http_reqs{name:Menu}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:Spontaneous Payement}': ['p(95)<15000'],
    'http_req_waiting{name:Spontaneous Payement}':['p(95)<15000'],
    'http_req_failed{name:Spontaneous Payement}':['rate<0.01'],
    'http_reqs{name:Spontaneous Payement}':[],

    //PAYMENT SCAN
    'http_req_duration{name:PaymentScan}': ['p(95)<15000'],
    'http_req_waiting{name:PaymentScan}':['p(95)<15000'],
    'http_req_failed{name:PaymentScan}':['rate<0.01'],
    'http_reqs{name:PaymentScan}':[],

    //EXIT
    'http_req_duration{name:Exit}': ['p(95)<15000'],
    'http_req_waiting{name:Exit}':['p(95)<15000'],
    'http_req_failed{name:Exit}':['rate<0.01'],
    'http_reqs{name:Exit}':[],

    //CONFIRM EXIT
    'http_req_duration{name:Confirm Exit}': ['p(95)<15000'],
    'http_req_waiting{name:Confirm Exit}':['p(95)<15000'],
    'http_req_failed{name:Confirm Exit}':['rate<0.01'],
    'http_reqs{name:Confirm Exit}':[],
  }
  
  
  export const average_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '2s', target: 20 },
      { duration: '5s', target: 20 },
      { duration: '5s', target: 0 },
    ],
  };
  
  export const low_load = {
      executor: 'ramping-vus',
      stages: [
        { duration: '1s', target: 1 },
        { duration: '1s', target: 0 },
      ],
    };