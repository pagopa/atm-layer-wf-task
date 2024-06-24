export const thresholdsSettings = {
    http_req_failed: [{ threshold: 'rate<0.01', abortOnFail: true }], 
    http_req_duration: ['p(99)<2000'],
  };
  
  export const nameThresholds={
    //MENU
    'http_req_duration{name:01 Menu iniziale}': ['avg<3700'],
    'http_req_waiting{name:01 Menu iniziale}':['avg<3700'],
    'http_req_failed{name:01 Menu iniziale}':['rate<0.01'],
    'http_reqs{name:01 Menu iniziale}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:02 Seleziona pagamento spontaneo}': ['avg<3700'],
    'http_req_waiting{name:02 Seleziona pagamento spontaneo}':['avg<3700'],
    'http_req_failed{name:02 Seleziona pagamento spontaneo}':['rate<0.01'],
    'http_reqs{name:02 Seleziona pagamento spontaneo}':[],

    //INSERT PAYMENT CODE
    'http_req_duration{name:03 Inserimento codice bollettino}': ['avg<3700'],
    'http_req_waiting{name:03 Inserimento codice bollettino}':['avg<3700'],
    'http_req_failed{name:03 Inserimento codice bollettino}':['rate<0.01'],
    'http_reqs{name:03 Inserimento codice bollettino}':[],

    //INSERT FISCALCODE EC
    'http_req_duration{name:04 Inserimento codice fiscale ente creditore}': ['avg<3700'],
    'http_req_waiting{name:04 Inserimento codice fiscale ente creditore}':['avg<3700'],
    'http_req_failed{name:04 Inserimento codice fiscale ente creditore}':['rate<0.01'],
    'http_reqs{name:04 Inserimento codice fiscale ente creditore}':[],

    //REVIEW PAYMENT
    'http_req_duration{name:05 Attivazione Bollettino}': ['avg<3700'],
    'http_req_waiting{name:05 Attivazione Bollettino}':['avg<3700'],
    'http_req_failed{name:05 Attivazione Bollettino}':['rate<0.01'],
    'http_reqs{name:05 Attivazione Bollettino}':[],

    //FEE CALCULATION
    'http_req_duration{name:06 Calcolo commissioni}': ['avg<3700'],
    'http_req_waiting{name:06 Calcolo commissioni}':['avg<3700'],
    'http_req_failed{name:06 Calcolo commissioni}':['rate<0.01'],
    'http_reqs{name:06 Calcolo commissioni}':[],

    //CONFIRM PAYMENT
    'http_req_duration{name:07 Conferma pagamento (preclose)}': ['avg<3700'],
    'http_req_waiting{name:07 Conferma pagamento (preclose)}':['avg<3700'],
    'http_req_failed{name:07 Conferma pagamento (preclose)}':['rate<0.01'],
    'http_reqs{name:07 Conferma pagamento (preclose)}':[],

    //ATHORIZE PAYMENT
    'http_req_duration{name:08 Autorizza pagamento (close)}': ['avg<3700'],
    'http_req_waiting{name:08 Autorizza pagamento (close)}':['avg<3700'],
    'http_req_failed{name:08 Autorizza pagamento (close)}':['rate<0.01'],
    'http_reqs{name:08 Autorizza pagamento (close)}':[],

    //STATUS PAYMENT
    'http_req_duration{name:09 Ricezione stato pagamento}': ['avg<3700'],
    'http_req_waiting{name:09 Ricezione stato pagamento}':['avg<3700'],
    'http_req_failed{name:09 Ricezione stato pagamento}':['rate<0.01'],
    'http_reqs{name:09 Ricezione stato pagamento}':[],

    //EXIT
    'http_req_duration{name:10 Seleziona uscita}': ['avg<3700'],
    'http_req_waiting{name:10 Seleziona uscita}':['avg<3700'],
    'http_req_failed{name:10 Seleziona uscita}':['rate<0.01'],
    'http_reqs{name:10 Seleziona uscita}':[],

    //CONFIRM EXIT
    'http_req_duration{name:11 Conferma uscita}': ['avg<3700'],
    'http_req_waiting{name:11 Conferma uscita}':['avg<3700'],
    'http_req_failed{name:11 Conferma uscita}':['rate<0.01'],
    'http_reqs{name:11 Conferma uscita}':[],
  }
  
  export const average_load = {
    executor: "per-vu-iterations",
    vus: 10,
    iterations: 10,
    startTime: "0s"
  };

  export const max_rate = {
    executor: "constant-arrival-rate",
    rate: 100,
    timeUnit: '1s',
    duration: '2m',
    startTime: "0s",
    preAllocatedVUs: 20, // Pre-allocate VUs before starting the test
    maxVUs: 700, // Maximum number of VUs
  }; 