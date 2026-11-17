# Cruscotto staff: affluenza per padiglione e picco stimato

Schermo per lo staff operativo in fiera. Per ogni padiglione (P1, P2, P3) mostra:

- **Presenti stimati adesso**
- **Picco stimato oggi**, con l'orario in cui si è verificato
- **Ingressi registrati oggi**

Si aggiorna ogni minuto e mostra l'ora dell'ultimo aggiornamento dei dati. Lo staff vede solo numeri
complessivi: nessun dato personale.

## Da dove arrivano i dati

`GET /edizioni/{id_edizione}/ingressi/andamento` (ruoli `staff` e `direzione`), con
`intervallo=1m` e `permanenza_minuti` (da 15 a 480, predefinito 120). Ogni punto della serie contiene
`codice_padiglione`, `inizio` (ora italiana), `ingressi_validi`, `ingressi_rifiutati` e, quando
disponibile, `presenti_stimati`.

## Regola del picco

- I varchi registrano solo gli **ingressi**, non le uscite: la presenza è una **stima**.
- **Presenti stimati** = persone entrate in quel padiglione nell'ultima **fascia di permanenza**.
  Valore proposto: **120 minuti** (da confermare con il cliente, punto aperto 2), configurabile.
- **Picco stimato** = valore massimo della stima nella giornata, **per padiglione** (non sul totale
  della fiera), con l'orario in cui si raggiunge. A parità vale il primo momento.
- **Family**: si conta una persona per ogni lettura valida, fino a 4.
- Staff, espositori e fornitori non passano dai varchi e non sono compresi.

## Dove vive il calcolo

Nel **backend** (campo `presenti_stimati`). Il frontend non ricalcola: legge il campo e ne ricava il
massimo giornaliero per mostrare il picco. Così il numero è identico in tutte le viste.
Se `presenti_stimati` manca (è facoltativo finché l'endpoint è in stato «proposta»), il cruscotto
mostra «Stima non disponibile» e solo gli ingressi: non inventa un valore.

## Limiti noti

- `ingressi_validi` conta le **letture** ai varchi, non le persone distinte. Letture ripetute dello
  stesso biglietto singolo dentro la finestra rischiano di essere contate due volte. Va risolto nel
  backend (biglietti distinti per i biglietti singoli): da concordare con chi sviluppa l'API.
- La data di rilascio di `presenti_stimati` non è nota.
- L'autenticazione non è decisa (punto aperto 4). `VITE_API_TOKEN` serve solo per prove in locale:
  un token nel frontend è visibile a chi apre la pagina.

## Come provarlo in locale (con dati inventati)

```bash
# 1. mock dell'API (Python 3.9+)
cd mock
python -m pip install -r requirements.txt
python -m pytest
python mock_api.py                      # http://localhost:8001
python mock_api.py --senza-presenti     # prova il caso senza il campo facoltativo

# 2. frontend (altro terminale)
cd frontend
npm install
npm test
npm run dev
```

Prove da fare a mano: fermare il mock e verificare che compaia l'avviso e che i numeri restino
sull'ultimo dato; riavviarlo e verificare che l'avviso sparisca da solo; ridimensionare la finestra
(schermo largo e piccolo); riavviare il mock con `--senza-presenti`.

## Configurazione

Vedi `frontend/.env.example`. Valori principali: `VITE_API_BASE_URL`, `VITE_ID_EDIZIONE`,
`VITE_PERMANENZA_MINUTI`, `VITE_REFRESH_MS`. Nessuna password o chiave va nel repository.

## Testo suggerito per `docs/glossario-kpi.md` (picco di presenza)

> **Picco di presenza.** Stima, per ciascun padiglione, delle persone entrate nell'ultima fascia di
> permanenza (predefinita 120 minuti, in attesa di conferma del cliente), calcolata sugli ingressi
> validi ai varchi. Il picco giornaliero è il valore massimo raggiunto in una giornata e l'orario in
> cui si verifica. Per i biglietti Family si conta una persona per ogni lettura valida, fino a 4.
> Staff, espositori e fornitori non sono compresi.
