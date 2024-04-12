export const thresholdsSettings = {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
    http_req_duration: ['p(99)<2000'],
  };
  
  export const nameThresholds={
    //MENU
    'http_req_duration{name:Menu iniziale}': ['p(95)<15000'],
    'http_req_waiting{name:Menu iniziale}':['p(95)<15000'],
    'http_req_failed{name:Menu iniziale}':['rate<0.01'],
    'http_reqs{name:Menu iniziale}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:Seleziona pagamento spontaneo}': ['p(95)<15000'],
    'http_req_waiting{name:Seleziona pagamento spontaneo}':['p(95)<15000'],
    'http_req_failed{name:Seleziona pagamento spontaneo}':['rate<0.01'],
    'http_reqs{name:Seleziona pagamento spontaneo}':[],

    //PAYMENT SCAN
    'http_req_duration{name:Inserimento codice bollettino}': ['p(95)<15000'],
    'http_req_waiting{name:Inserimento codice bollettino}':['p(95)<15000'],
    'http_req_failed{name:Inserimento codice bollettino}':['rate<0.01'],
    'http_reqs{name:Inserimento codice bollettino}':[],

    //EXIT
    'http_req_duration{name:Seleziona uscita}': ['p(95)<15000'],
    'http_req_waiting{name:Seleziona uscita}':['p(95)<15000'],
    'http_req_failed{name:Seleziona uscita}':['rate<0.01'],
    'http_reqs{name:Seleziona uscita}':[],

    //CONFIRM EXIT
    'http_req_duration{name:Conferma uscita}': ['p(95)<15000'],
    'http_req_waiting{name:Conferma uscita}':['p(95)<15000'],
    'http_req_failed{name:Conferma uscita}':['rate<0.01'],
    'http_reqs{name:Conferma uscita}':[],
  }
  
  
//  export const average_load = {
//     executor: 'ramping-vus',
//     stages: [
//       { duration: '2s', target: 5 },
//       { duration: '10s', target: 10 },
//       { duration: '5m', target: 40 },
//       { duration: '5m', target: 3 },
//       { duration: '2s', target: 0 },
//     ],
//   }; 



  export const average_load = {
    executor: "per-vu-iterations",
    vus: 10,
    iterations: 10,
    startTime: "0s",
  };
  /*export const average_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '10m', target: 1 }
    ],
  };*/
  
  export const low_load = {
      executor: 'constant-arrival-rate',
      stages: [
        { duration: '1s', target: 1 },
        { duration: '1s', target: 0 },
      ],
    };