export const thresholdsSettings = {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
    http_req_duration: ['p(99)<2000'],
  };
  
  export const nameThresholds={
    //MENU
    'http_req_duration{name:1 Menu iniziale}': ['p(95)<3700'],
    'http_req_waiting{name:1 Menu iniziale}':['p(95)<3700'],
    'http_req_failed{name:1 Menu iniziale}':['rate<0.01'],
    'http_reqs{name:1 Menu iniziale}':[],

    // //MESSAGES
    // 'http_req_duration{name:2 Recupera messaggi di cortesia}': ['p(95)<3700'],
    // 'http_req_waiting{name:2 Recupera messaggi di cortesia}':['p(95)<3700'],
    // 'http_req_failed{name:2 Recupera messaggi di cortesia}':['rate<0.01'],
    // 'http_reqs{name:2 Recupera messaggi di cortesia}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:2 Seleziona pagamento spontaneo}': ['p(95)<3700'],
    'http_req_waiting{name:2 Seleziona pagamento spontaneo}':['p(95)<3700'],
    'http_req_failed{name:2 Seleziona pagamento spontaneo}':['rate<0.01'],
    'http_reqs{name:2 Seleziona pagamento spontaneo}':[],

    // //SELECT SPONTANEOUS PAYEMENT
    // 'http_req_duration{name:4 Seleziona inserisci dati bollettino}': ['p(95)<3700'],
    // 'http_req_waiting{name:4 Seleziona inserisci dati bollettino}':['p(95)<3700'],
    // 'http_req_failed{name:4 Seleziona inserisci dati bollettino}':['rate<0.01'],
    // 'http_reqs{name:4 Seleziona inserisci dati bollettino}':[],

    //PAYMENT SCAN
    // 'http_req_duration{name:Inserimento codice bollettino}': ['p(95)<3700'],
    // 'http_req_waiting{name:Inserimento codice bollettino}':['p(95)<3700'],
    // 'http_req_failed{name:Inserimento codice bollettino}':['rate<0.01'],
    // 'http_reqs{name:Inserimento codice bollettino}':[],

    //INSERT PAYMENT CODE
    'http_req_duration{name:3 Inserimento codice bollettino}': ['p(95)<3700'],
    'http_req_waiting{name:3 Inserimento codice bollettino}':['p(95)<3700'],
    'http_req_failed{name:3 Inserimento codice bollettino}':['rate<0.01'],
    'http_reqs{name:3 Inserimento codice bollettino}':[],

    //INSERT FISCALCODE EC
    'http_req_duration{name:4 Inserimento codice fiscale ente creditore}': ['p(95)<3700'],
    'http_req_waiting{name:4 Inserimento codice fiscale ente creditore}':['p(95)<3700'],
    'http_req_failed{name:4 Inserimento codice fiscale ente creditore}':['rate<0.01'],
    'http_reqs{name:4 Inserimento codice fiscale ente creditore}':[],

    //REVIEW PAYMENT
    'http_req_duration{name:5 Attivazione Bollettino}': ['p(95)<3700'],
    'http_req_waiting{name:5 Attivazione Bollettino}':['p(95)<3700'],
    'http_req_failed{name:5 Attivazione Bollettino}':['rate<0.01'],
    'http_reqs{name:5 Attivazione Bollettino}':[],

    //CONFIRM PAYMENT
    'http_req_duration{name:6 Conferma pagamento (preclose)}': ['p(95)<3700'],
    'http_req_waiting{name:6 Conferma pagamento (preclose)}':['p(95)<3700'],
    'http_req_failed{name:6 Conferma pagamento (preclose)}':['rate<0.01'],
    'http_reqs{name:6 Conferma pagamento (preclose)}':[],

    //ATHORIZE PAYMENT
    'http_req_duration{name:7 Autorizza pagamento (close)}': ['p(95)<3700'],
    'http_req_waiting{name:7 Autorizza pagamento (close)}':['p(95)<3700'],
    'http_req_failed{name:7 Autorizza pagamento (close)}':['rate<0.01'],
    'http_reqs{name:7 Autorizza pagamento (close)}':[],

    //EXIT
    'http_req_duration{name:8 Seleziona uscita}': ['p(95)<3700'],
    'http_req_waiting{name:8 Seleziona uscita}':['p(95)<3700'],
    'http_req_failed{name:8 Seleziona uscita}':['rate<0.01'],
    'http_reqs{name:8 Seleziona uscita}':[],

    //CONFIRM EXIT
    'http_req_duration{name:9 Conferma uscita}': ['p(95)<3700'],
    'http_req_waiting{name:9 Conferma uscita}':['p(95)<3700'],
    'http_req_failed{name:9 Conferma uscita}':['rate<0.01'],
    'http_reqs{name:9 Conferma uscita}':[],
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
        { duration: '50s', target: 1 },
        { duration: '1s', target: 0 },
      ],
    };