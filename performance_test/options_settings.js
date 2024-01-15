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
    'http_req_duration{name:Bill Payment}': ['p(95)<15000'],
    'http_req_waiting{name:Bill Payment}':['p(95)<15000'],
    'http_req_failed{name:Bill Payment}':['rate<0.01'],
    'http_reqs{name:Bill Payment}':[],

    //PAYMENT SCAN
    'http_req_duration{name:Calculate Fees}': ['p(95)<15000'],
    'http_req_waiting{name:Calculate Fees}':['p(95)<15000'],
    'http_req_failed{name:Calculate Fees}':['rate<0.01'],
    'http_reqs{name:Calculate Fees}':[],

    //EXIT
    'http_req_duration{name:Pre Close}': ['p(95)<15000'],
    'http_req_waiting{name:Pre Close}':['p(95)<15000'],
    'http_req_failed{name:Pre Close}':['rate<0.01'],
    'http_reqs{name:Pre Close}':[],

    //CONFIRM EXIT
    'http_req_duration{name:Close}': ['p(95)<15000'],
    'http_req_waiting{name:Close}':['p(95)<15000'],
    'http_req_failed{name:Close}':['rate<0.01'],
    'http_reqs{name:Close}':[],
  }
  
  
  export const average_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '2s', target: 5 },
      { duration: '10s', target: 10 },
      { duration: '5m', target: 20 },
      { duration: '5m', target: 3 },
      { duration: '2s', target: 0 },
    ],
  };
  
  export const low_load = {
      executor: 'ramping-vus',
      stages: [
        { duration: '1s', target: 1 },
        { duration: '1s', target: 0 },
      ],
    };