export const thresholdsSettings = {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
    http_req_duration: ['p(99)<2000'],
  };
  
  export const nameThresholds={
    //MENU
    'http_req_duration{name:01 Menu iniziale}': ['p(95)<3700'],
    'http_req_waiting{name:01 Menu iniziale}':['p(95)<3700'],
    'http_req_failed{name:01 Menu iniziale}':['rate<0.01'],
    'http_reqs{name:01 Menu iniziale}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:02 Seleziona pagamento spontaneo}': ['p(95)<3700'],
    'http_req_waiting{name:02 Seleziona pagamento spontaneo}':['p(95)<3700'],
    'http_req_failed{name:02 Seleziona pagamento spontaneo}':['rate<0.01'],
    'http_reqs{name:02 Seleziona pagamento spontaneo}':[],

    //INSERT PAYMENT CODE
    'http_req_duration{name:03 Inserimento codice bollettino}': ['p(95)<3700'],
    'http_req_waiting{name:03 Inserimento codice bollettino}':['p(95)<3700'],
    'http_req_failed{name:03 Inserimento codice bollettino}':['rate<0.01'],
    'http_reqs{name:03 Inserimento codice bollettino}':[],

    //INSERT FISCALCODE EC
    'http_req_duration{name:04 Inserimento codice fiscale ente creditore}': ['p(95)<3700'],
    'http_req_waiting{name:04 Inserimento codice fiscale ente creditore}':['p(95)<3700'],
    'http_req_failed{name:04 Inserimento codice fiscale ente creditore}':['rate<0.01'],
    'http_reqs{name:04 Inserimento codice fiscale ente creditore}':[],

    //REVIEW PAYMENT
    'http_req_duration{name:05 Attivazione Bollettino}': ['p(95)<3700'],
    'http_req_waiting{name:05 Attivazione Bollettino}':['p(95)<3700'],
    'http_req_failed{name:05 Attivazione Bollettino}':['rate<0.01'],
    'http_reqs{name:05 Attivazione Bollettino}':[],

    //FEE CALCULATION
    'http_req_duration{name:06 Calcolo commissioni}': ['p(95)<3700'],
    'http_req_waiting{name:06 Calcolo commissioni}':['p(95)<3700'],
    'http_req_failed{name:06 Calcolo commissioni}':['rate<0.01'],
    'http_reqs{name:06 Calcolo commissioni}':[],

    //CONFIRM PAYMENT
    'http_req_duration{name:07 Conferma pagamento (preclose)}': ['p(95)<3700'],
    'http_req_waiting{name:07 Conferma pagamento (preclose)}':['p(95)<3700'],
    'http_req_failed{name:07 Conferma pagamento (preclose)}':['rate<0.01'],
    'http_reqs{name:07 Conferma pagamento (preclose)}':[],

    //ATHORIZE PAYMENT
    'http_req_duration{name:08 Autorizza pagamento (close)}': ['p(95)<3700'],
    'http_req_waiting{name:08 Autorizza pagamento (close)}':['p(95)<3700'],
    'http_req_failed{name:08 Autorizza pagamento (close)}':['rate<0.01'],
    'http_reqs{name:08 Autorizza pagamento (close)}':[],

    //STATUS PAYMENT
    'http_req_duration{name:09 Ricezione stato pagamento}': ['p(95)<3700'],
    'http_req_waiting{name:09 Ricezione stato pagamento}':['p(95)<3700'],
    'http_req_failed{name:09 Ricezione stato pagamento}':['rate<0.01'],
    'http_reqs{name:09 Ricezione stato pagamento}':[],

    //EXIT
    'http_req_duration{name:10 Seleziona uscita}': ['p(95)<3700'],
    'http_req_waiting{name:10 Seleziona uscita}':['p(95)<3700'],
    'http_req_failed{name:10 Seleziona uscita}':['rate<0.01'],
    'http_reqs{name:10 Seleziona uscita}':[],

    //CONFIRM EXIT
    'http_req_duration{name:11 Conferma uscita}': ['p(95)<3700'],
    'http_req_waiting{name:11 Conferma uscita}':['p(95)<3700'],
    'http_req_failed{name:11 Conferma uscita}':['rate<0.01'],
    'http_reqs{name:11 Conferma uscita}':[],
  }
  
  export const average_load = {
    executor: 'ramping-vus',
    stages: [
      { duration: '2s', target: 5 },
      { duration: '10s', target: 10 },
      { duration: '5m', target: 40 },
      { duration: '5m', target: 3 },
      { duration: '2s', target: 0 },
    ],
  };
  
  // export const average_load = {
  //   executor: "per-vu-iterations",
  //   vus: 40,
  //   iterations: 10,
  //   startTime: "0s",
  // };

  export const low_load = {
      executor: 'ramping-vus',
      stages: [
        { duration: '1m', target: 1 },
        { duration: '1s', target: 0 },
      ],
    };