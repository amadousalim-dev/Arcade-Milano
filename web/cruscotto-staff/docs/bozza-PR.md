## Cosa fa
Cruscotto staff con affluenza per padiglione (P1, P2, P3): presenti stimati adesso, picco stimato
di oggi con orario, ingressi registrati oggi. Aggiornamento ogni minuto e ora dell'ultimo
aggiornamento sempre visibile.

Closes #NUMERO_ISSUE

## Regola del picco
Come da glossario KPI (`docs/glossario-kpi.md`): stima per padiglione sugli ingressi validi
dell'ultima fascia di permanenza (120 minuti, configurabile, da confermare con il cliente);
picco = massimo giornaliero con orario. Family: una persona per lettura valida, fino a 4.

## Scelte da sapere
- Il calcolo vive nel backend (`presenti_stimati`): il frontend lo mostra e ne ricava il massimo
  giornaliero, senza ricalcolare.
- Se `presenti_stimati` manca, il cruscotto mostra «Stima non disponibile» e solo gli ingressi.
- Il mock in `mock/` usa solo dati inventati; il suo calcolo serve unicamente alle prove.

## Limiti noti
- `ingressi_validi` conta le letture, non le persone distinte: letture ripetute dello stesso
  biglietto singolo nella finestra possono essere contate due volte (da risolvere nel backend).
- Autenticazione non definita: `VITE_API_TOKEN` solo per prove in locale.

## Come l'ho provato
- [ ] `npm test` e `python -m pytest` passano
- [ ] Aggiornamento automatico ogni minuto verificato
- [ ] Mock fermato: avviso visibile, ultimi dati mantenuti, ripresa automatica
- [ ] Prova con `--senza-presenti`
- [ ] Schermo grande e piccolo (leggibilità)

## Checklist
- [ ] Nessun dato personale, password, chiave o screenshot
- [ ] Documentazione aggiornata (`docs/cruscotto-staff.md`, glossario KPI)
- [ ] Due revisori diversi dall'autore, entro 24 ore lavorative
