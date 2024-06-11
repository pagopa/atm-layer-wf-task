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

    //MESSAGES
    'http_req_duration{name:Recupera messaggi di cortesia e posizione debitoria}': ['p(95)<15000'],
    'http_req_waiting{name:Recupera messaggi di cortesia e posizione debitoria}':['p(95)<15000'],
    'http_req_failed{name:Recupera messaggi di cortesia e posizione debitoria}':['rate<0.01'],
    'http_reqs{name:Recupera messaggi di cortesia e posizione debitoria}':[],

    //SPONTANEOUS PAYEMENT
    'http_req_duration{name:Seleziona pagamento spontaneo}': ['p(95)<15000'],
    'http_req_waiting{name:Seleziona pagamento spontaneo}':['p(95)<15000'],
    'http_req_failed{name:Seleziona pagamento spontaneo}':['rate<0.01'],
    'http_reqs{name:Seleziona pagamento spontaneo}':[],

    //PAYMENT SCAN
    // 'http_req_duration{name:Inserimento codice bollettino}': ['p(95)<15000'],
    // 'http_req_waiting{name:Inserimento codice bollettino}':['p(95)<15000'],
    // 'http_req_failed{name:Inserimento codice bollettino}':['rate<0.01'],
    // 'http_reqs{name:Inserimento codice bollettino}':[],

    //INSERT PAYMENT CODE
    'http_req_duration{name:Inserimento codice bollettino}': ['p(95)<15000'],
    'http_req_waiting{name:Inserimento codice bollettino}':['p(95)<15000'],
    'http_req_failed{name:Inserimento codice bollettino}':['rate<0.01'],
    'http_reqs{name:Inserimento codice bollettino}':[],

    //INSERT FISCALCODE EC
    'http_req_duration{name:Inserimento codice fiscale ente creditore}': ['p(95)<15000'],
    'http_req_waiting{name:Inserimento codice fiscale ente creditore}':['p(95)<15000'],
    'http_req_failed{name:Inserimento codice fiscale ente creditore}':['rate<0.01'],
    'http_reqs{name:Inserimento codice fiscale ente creditore}':[],

    //REVIEW PAYMENT
    'http_req_duration{name:Riepilogo dati avviso}': ['p(95)<15000'],
    'http_req_waiting{name:Riepilogo dati avviso}':['p(95)<15000'],
    'http_req_failed{name:Riepilogo dati avviso}':['rate<0.01'],
    'http_reqs{name:Riepilogo dati avviso}':[],

    //CONFIRM PAYMENT
    'http_req_duration{name:Conferma pagamento avviso}': ['p(95)<15000'],
    'http_req_waiting{name:Conferma pagamento avviso}':['p(95)<15000'],
    'http_req_failed{name:Conferma pagamento avviso}':['rate<0.01'],
    'http_reqs{name:Conferma pagamento avviso}':[],

    //ATHORIZE PAYMENT
    'http_req_duration{name:Autorizza pagamento avviso}': ['p(95)<15000'],
    'http_req_waiting{name:Autorizza pagamento avviso}':['p(95)<15000'],
    'http_req_failed{name:Autorizza pagamento avviso}':['rate<0.01'],
    'http_reqs{name:Autorizza pagamento avviso}':[],

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