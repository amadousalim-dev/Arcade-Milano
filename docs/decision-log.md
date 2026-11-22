# Decision log – ARCADE Milano (Gruppo 1)

Registro delle decisioni importanti del progetto, come previsto dalle regole del gruppo (sezione 5 e sezione 4, «Contratto dati»).

**Come si usa**

- Ogni decisione ha: **data, decisione, motivo, chi**. In più lo **stato**: Applicata, Proposta, Da concordare, Superata.
- Le decisioni sono raggruppate per area. Ogni area ha un prefisso per gli id (il back-office usa `D-`); chi apre una nuova area sceglie un prefisso e lo aggiunge qui sotto.
- Le *change request* sullo schema dati e sull'API (rinominare o eliminare dopo il congelamento del 30 ottobre) si registrano qui, con l'approvazione dei responsabili di entrambi i percorsi.
- Il file si modifica con una PR, come il resto del repository. Non contiene dati personali né credenziali.

| Area | Prefisso | Responsabile |
|---|---|---|
| Back-office Power Apps | `D-` | Vittorio |

---

## Back-office Power Apps

Decisioni prese durante lo sviluppo del back-office commerciale (task 1.7, 1.8, 2.1) e dell'integrazione con il database Supabase e i flussi Power Automate, dall'11 al 20 novembre 2026.

### Riepilogo

| ID | Data | Decisione | Stato |
|---|---|---|---|
| D-01 | 11/11 | Stato di stand e pagamenti calcolato nell'app | Applicata |
| D-02 | 11/11 | Codici di stato uguali a quelli delle viste | Applicata |
| D-03 | 11/11 | Chiusura delle vendite calcolata (apertura − 15 giorni) | Applicata (temporanea) |
| D-04 | 11/11 | Change request: colonne mancanti rispetto al dizionario v0.6 | Da concordare |
| D-05 | 11/11 | Regola per il preventivo in approvazione che non scade | Proposta al cliente |
| D-06 | 11/11 | Niente connettore personalizzato per leggere le viste (per il SAL) | Applicata |
| D-07 | 11/11 | Demo del SAL 2: contratto 2026 firmato prima della chiusura vendite | Proposta |
| D-08 | 12/11 | Id dei preventivi: «massimo + 1» (abbandonato il numero data-ora) | Applicata |
| D-09 | 12/11 | Id dei contratti: `C<anno>-NNNN` | Applicata |
| D-10 | 12/11 | Colonne `NOT NULL` sempre esplicite negli inserimenti | Applicata |
| D-11 | 12/11 | Annullamento manuale delle scritture in caso di errore | Applicata |
| D-12 | 12/11 | Pagamenti alla firma creati dall'app (piano B) | Superata da D-19 |
| D-13 | 13/11 | Dati di riferimento in collezioni caricate all'avvio | Applicata |
| D-14 | 13/11 | App collegata al database come `postgres` per il SAL | Applicata (temporanea) |
| D-15 | 13/11 | Script dei ruoli eseguito; `u_powerapps` richiede `DELETE` | Da concordare |
| D-16 | 13/11 | Flusso pagamenti v1 (API REST su `public`) non integrabile | Applicata |
| D-17 | 16/11 | Un solo metodo di generazione degli id per ogni tabella | Applicata |
| D-18 | 16/11 | Pagamenti con il contatore del database; `setval` al passaggio | Applicata |
| D-19 | 18/11 | Pagamenti creati dal flusso «Crea pagamenti alla firma» | Applicata |
| D-20 | 18/11 | Flussi generici `db_*` non integrati nel back-office | Applicata |
| D-21 | 18/11 | Tutto il BAD nello stesso ambiente Power Platform | Proposta |
| D-22 | 19/11 | Flussi portati nell'ambiente tramite soluzione, non pacchetto legacy | Applicata |
| D-23 | 19/11 | Approvare uno sconto = inviare il preventivo | Applicata |
| D-24 | 19/11 | Utente corrente letto dalla tabella `utente` (`gblUtente`) | Applicata |
| D-25 | 19/11 | Disponibilità sponsorizzazioni: venduti + impegnati nei preventivi aperti | Applicata |
| D-26 | 19/11 | Doppio controllo delle sponsorizzazioni all'invio | Applicata |
| D-27 | 19/11 | Filtri sul database solo semplici; il resto nell'app | Applicata |
| D-28 | 19/11 | Preventivo aperto letto una volta in `gblPreventivo` | Applicata |

### 11 novembre 2026

#### D-01 – Stato di stand e pagamenti calcolato nell'app
- **Decisione:** lo stato degli stand (`venduto` > `opzionato` > `in_prelazione` > `libero`) e dei pagamenti si calcola nell'app (`btnCalcolaStati`, `scrContratto`) con le stesse regole delle viste `v_stato_stand` e `v_pagamento_stato`.
- **Motivo:** il connettore PostgreSQL di Power Apps non mostra le viste (verificato). Le regole restano identiche a quelle del database, così i numeri tornano con il report Power BI (verificati: 2024 76,5% stand / 66,1% m²; 2025 84,6% / 70,8%).
- **Chi:** Vittorio. **Stato:** Applicata. Se in futuro le viste saranno leggibili (API del BID), si sostituisce solo il calcolo dello stato.

#### D-02 – Codici di stato uguali a quelli delle viste
- **Decisione:** l'app usa i codici con il trattino basso (`in_prelazione`, `in_approvazione`, `da_pagare`, `pagato_in_ritardo`…) e li mostra a video con `Substitute(stato; "_"; " ")`.
- **Motivo:** un solo vocabolario tra database, viste, app e report.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-03 – Chiusura delle vendite calcolata
- **Decisione:** in `App.Formulas`, `ChiusuraVendite = DateAdd(EdizioneSel.data_inizio; -15; TimeUnit.Days)` (2026: 03/12/2026). Dopo questa data l'app non permette di inviare, approvare o firmare.
- **Motivo:** regola confermata dal cliente (11/11); la colonna `edizione.chiusura_vendite` prevista dal dizionario v0.6 non esiste ancora nel database.
- **Chi:** Vittorio. **Stato:** Applicata, temporanea fino a D-04.

#### D-04 – Change request: colonne mancanti
- **Decisione:** chiedere l'aggiunta (solo colonne nuove, ammessa dopo il congelamento) di `edizione.chiusura_vendite`, `preventivo.in_approvazione_dal`, `variazione_contratto.conguaglio_fuori_piattaforma`, `sollecito.registrato_da`, `contatto_stand.verificato_il`.
- **Motivo:** lo script `arcade_supabase_struttura.sql` deriva dal dizionario v0.4; il v0.6 e le risposte del cliente prevedono queste colonne.
- **Chi:** Vittorio → Jean-Patrick. **Stato:** Da concordare (al 19/11 la tabella `preventivo` non ha ancora `in_approvazione_dal`).

#### D-05 – Preventivo in approvazione che non scade
- **Decisione (proposta):** dopo 3 giorni in approvazione avviso al direttore commerciale; dopo 7 giorni ritorno in bozza e stand liberato. Giorni come parametri (`GiorniAvvisoApprovazione`, `GiorniMaxApprovazione`). Applicazione con il flusso giornaliero (2.4, Matteo).
- **Motivo:** il cliente ha chiesto una regola, perché i 30 giorni di validità partono dall'invio e un preventivo in approvazione bloccherebbe lo stand senza limite.
- **Chi:** gruppo BAD. **Stato:** Proposta al cliente; richiede `in_approvazione_dal` (D-04).

#### D-06 – Niente connettore personalizzato per leggere le viste
- **Decisione:** per il SAL non si usa un connettore personalizzato verso l'API REST di Supabase per leggere le viste.
- **Motivo:** con la RLS attiva servirebbe la chiave `service_role`, che salta ogni protezione e finirebbe in un connettore condiviso; due connessioni da mantenere; il calcolo nell'app (D-01) è già pronto.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-07 – Demo del SAL 2 e chiusura delle vendite
- **Decisione (proposta):** il SAL 2 è spostato al 4/12 e le vendite 2026 chiudono il 3/12. Il contratto 2026 da mostrare si firma davvero tra il 30/11 e il 2/12; al SAL si mostra già firmato, con un preventivo aperto e il pulsante di firma disattivato.
- **Motivo:** la regola della chiusura vendite è del cliente e non va aggirata; mostrarla bloccata è una dimostrazione che le regole funzionano.
- **Chi:** Vittorio. **Stato:** Proposta; da chiedere al cliente se vuole comunque una firma dal vivo.

### 12 novembre 2026

#### D-08 – Id dei preventivi: «massimo + 1»
- **Decisione:** `id_preventivo = Coalesce(Max('arcade.preventivo'; id_preventivo); 0) + 1`, calcolato in `btnSalvaBozza`.
- **Motivo:** il connettore PostgreSQL vuole la chiave primaria esplicita negli inserimenti anche per le colonne `IDENTITY`. La prima soluzione (numero data-ora di 15 cifre) è stata abbandonata: con id così lunghi i filtri non ritrovavano i record e la firma duplicava le righe. In caso di salvataggio contemporaneo la chiave primaria rifiuta il doppione e l'utente riprova.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-09 – Id dei contratti
- **Decisione:** `id_contratto = "C" & anno & "-" & progressivo a 4 cifre`, con progressivo = massimo dell'edizione + 1, calcolato al momento della firma (non in un'etichetta).
- **Motivo:** coerenza con i 198 contratti storici; calcolato all'ultimo momento per leggere il valore più aggiornato; un contratto disdetto non fa riusare il numero.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-10 – Colonne `NOT NULL` sempre esplicite
- **Decisione:** ogni `Patch` di inserimento passa tutte le colonne `NOT NULL`, anche quelle con un valore predefinito nel database (es. `pagamento.data_dedotta: false; annullato: false`).
- **Motivo:** il connettore PostgreSQL non applica i valori predefiniti del database (errore «Il campo 'annullato' è obbligatorio»).
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-11 – Annullamento manuale in caso di errore
- **Decisione:** salvataggio della bozza e firma scrivono in sequenza (testata, righe, pagamenti) dentro `IfError`; il ramo di errore cancella ciò che è stato scritto e rimette il preventivo allo stato precedente. Nel ramo «tutto bene» e nel ramo «errore» le azioni si separano con `;;`, tra i due rami un solo `;`, entrambi finiscono con `Notify`.
- **Motivo:** il connettore non offre transazioni su più tabelle. Un `;` singolo di troppo aveva trasformato la cancellazione nel risultato predefinito di `IfError`, che girava **senza** errori e cancellava bozze e contratti appena salvati.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-12 – Pagamenti alla firma creati dall'app (piano B)
- **Decisione:** finché il flusso 1.9 non funziona, `btnFirma` crea acconto (30% arrotondato al centesimo) e saldo (imponibile − acconto) con le regole del cliente, compresa la firma tardiva (se l'acconto scadrebbe dopo il saldo, entrambi scadono 15 giorni prima dell'apertura).
- **Motivo:** permettere di provare la firma senza attendere il flusso.
- **Chi:** Vittorio. **Stato:** Superata da D-19.

### 13 novembre 2026

#### D-13 – Dati di riferimento caricati all'avvio
- **Decisione:** `App.OnStart` carica `colEspositori` e `colEdizioni`; `App.StartScreen = scrStand`; protezione dalla divisione per zero nei contatori; edizione di riserva `gblEdVista` se il menu non è ancora pronto.
- **Motivo:** all'avvio lo Studio calcola tutte le schermate e il connettore a volte risponde «record non trovato» su una tabella; 135 espositori e 3 edizioni cambiano raramente; meno richieste al database.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-14 – Connessione come `postgres` per il SAL
- **Decisione:** l'app si collega al database con l'utente `postgres` fino al SAL.
- **Motivo:** la RLS è attiva su tutte le 22 tabelle (verificato); `postgres` è il proprietario e non è bloccato. Il passaggio a un utente con permessi limitati è previsto in 2.3 (vedi D-15).
- **Chi:** Vittorio. **Stato:** Applicata, temporanea.

#### D-15 – Script dei ruoli e permessi di `u_powerapps`
- **Decisione:** lo script `arcade_ruoli_accesso.sql` è stato eseguito (utenti `u_api_backend`, `u_powerapps`, `u_powerbi`, `u_caricamento`, `u_privacy`). Per collegare l'app con `u_powerapps` serve aggiungere `DELETE` su `preventivo`, `contratto` e `pagamento`, usato solo dagli annullamenti (D-11).
- **Motivo:** senza `DELETE` una firma fallita lascerebbe dati a metà.
- **Chi:** gruppo BAD → BID. **Stato:** Da concordare.

#### D-16 – Flusso pagamenti v1 non integrabile
- **Decisione:** la prima versione del flusso di Matteo non si integra; inviate le correzioni.
- **Motivo:** cercava tabelle `arcade_*` nello schema `public` (inesistenti); usava la chiave publishable, che con la RLS attiva non legge né scrive; mancavano firma tardiva, arrotondamento, formato delle date, risposta `esito`/`messaggio` e risposta in caso di errore.
- **Chi:** Vittorio, Matteo. **Stato:** Applicata.

### 15 novembre 2026

- PR delle task 1.7 e 1.8 unita su `main` (Salim). Nessuna decisione nuova.

### 16 novembre 2026

#### D-17 – Un solo metodo di generazione degli id per tabella
- **Decisione:** in ogni tabella gli id li genera un solo sistema, sempre con lo stesso metodo: `preventivo` e righe → app («massimo + 1»); `contratto` → app (`C<anno>-NNNN`); `pagamento` → flusso (contatore del database).
- **Motivo:** gli id scritti esplicitamente non fanno avanzare il contatore `IDENTITY`; chi poi usa il contatore riceve numeri già occupati («chiave duplicata»).
- **Chi:** Vittorio. **Stato:** Applicata. Da concordare con il BID se il backend inserirà preventivi (servirebbe «massimo + 1» o un `setval` prima).

#### D-18 – Pagamenti con il contatore; `setval` al passaggio
- **Decisione:** il flusso inserisce i pagamenti via API REST senza indicare l'id; al passaggio dal piano B si riallinea una volta il contatore con `setval(pg_get_serial_sequence('arcade.pagamento','id_pagamento'), (select max(id_pagamento) from arcade.pagamento))`. Il piano B non si rimette come riserva.
- **Motivo:** il piano B aveva inserito pagamenti con id scelti dall'app oltre il contatore.
- **Chi:** Vittorio. **Stato:** Applicata.

### 18 novembre 2026

#### D-19 – Pagamenti creati dal flusso «Crea pagamenti alla firma»
- **Decisione:** la versione del 18/11 del flusso è approvata e sostituisce il piano B. `btnFirma` chiama `Crea_pagamenti_alla_firma.Run(idC; Text(Today(); "yyyy-mm-dd"))`; se `esito <> "ok"` genera un errore che fa annullare contratto e righe (niente contratti senza pagamenti).
- **Motivo:** il flusso ora usa lo schema `arcade`, la regola della firma tardiva, l'arrotondamento al centesimo, le date `yyyy-MM-dd`, `data_dedotta` predefinito (`false`) e una risposta di errore che parte solo se l'inserimento fallisce. Le regole dei pagamenti stanno in un solo posto.
- **Chi:** Matteo (flusso), Vittorio (integrazione). **Stato:** Applicata. Aperti: connessione «Invoker» (proposta: «Utenti solo esecuzione» con la connessione di Matteo) e quale chiave usa il connettore personalizzato.

#### D-20 – Flussi generici `db_leggi`, `db_scrivi`, `db_aggiorna`, `db_eliminare` non integrati
- **Decisione:** il back-office continua a usare il connettore PostgreSQL diretto.
- **Motivo:** restituiscono l'input invece del risultato; `db_leggi` e `db_eliminare` non puntano allo schema `arcade`; accettano qualsiasi tabella e filtro con la chiave del connettore (rischio per privacy e ruoli); un filtro vuoto potrebbe cancellare una tabella; romperebbero D-17. Unico uso possibile: leggere le viste, dopo correzioni e con un elenco fisso.
- **Chi:** Vittorio. **Stato:** Applicata. Suggerito a Matteo di concentrarsi su 2.4 e 2.5.

#### D-21 – Tutto il BAD nello stesso ambiente
- **Decisione (proposta):** flussi e app nello stesso ambiente («Ars Digital – Laboratorio») e nella stessa soluzione; i flussi restano di Matteo, che aggiunge Vittorio come comproprietario.
- **Motivo:** un'app chiama solo flussi del suo ambiente; due copie dello stesso flusso divergono; le regole del gruppo vogliono un solo proprietario per file binario.
- **Chi:** gruppo BAD. **Stato:** Proposta.

### 19 novembre 2026

#### D-22 – Flussi portati nell'ambiente tramite soluzione
- **Decisione:** il flusso di Matteo (ambiente predefinito ITS) si porta nell'ambiente del back-office con una soluzione esportata e importata, che deve contenere anche il connettore personalizzato «SUPABASE REST».
- **Motivo:** l'import dei pacchetti legacy è disabilitato dall'amministratore («Crea in soluzioni Dataverse»); il riferimento di connessione da solo non basta; aggiungere il connettore alla soluzione è permesso solo al proprietario dell'API o all'amministratore dell'ambiente. In alternativa: ricreare connettore e flusso direttamente nell'ambiente del back-office.
- **Chi:** Vittorio, Matteo. **Stato:** Applicata (in corso).

#### D-23 – Approvare uno sconto equivale a inviare il preventivo
- **Decisione:** in `scrApprovazioni`, «Approva» porta il preventivo da `in_approvazione` a `inviato`, con `approvato_da`, `approvato_il`, `inviato_il` e `valido_fino` = oggi + 30; «Rimanda in bozza» lo porta a `bozza` (confermato dal cliente) e libera lo stand.
- **Motivo:** il commerciale aveva già inviato il preventivo, che attendeva solo il via libera; il flusso di invio e-mail (2.4) partirà dallo stesso passaggio.
- **Chi:** Vittorio. **Stato:** Applicata (verificata: preventivo n. 3).

#### D-24 – Utente corrente dalla tabella `utente`
- **Decisione:** `App.OnStart` imposta `gblUtente = LookUp('arcade.utente'; Lower(email) = Lower(User().Email))`; serve per `approvato_da` e sarà la base dei permessi per ruolo (2.3). Le righe degli utenti si inseriscono dallo SQL Editor e non vanno nel repository.
- **Motivo:** `approvato_da` è un riferimento alla tabella `utente`; l'app usa una sola connessione, quindi chi può fare cosa lo decide l'app.
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-25 – Disponibilità delle sponsorizzazioni
- **Decisione:** disponibili = `quantita_disponibile` − venduti nei contratti attivi − impegnati nei preventivi aperti (`in_approvazione` o `inviato` non scaduto), escluso il preventivo in modifica; le bozze non impegnano. Calcolo unico in `btnCaricaImpegni` (`colSponsorOccupati`); `btnAggiungiRiga` sottrae anche le righe già nel preventivo.
- **Motivo:** due preventivi aperti non devono poter offrire la stessa sponsorizzazione unica (es. Main Stage).
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-26 – Doppio controllo delle sponsorizzazioni all'invio
- **Decisione:** `btnInvia` ricalcola la disponibilità con dati freschi prima di inviare, scritto direttamente nel pulsante.
- **Motivo:** tra l'aggiunta della riga e l'invio un collega può aver inviato la stessa sponsorizzazione. Non si può riusare `btnCaricaImpegni` con `Select`, perché `Select` esegue l'azione solo dopo la fine della formula corrente: è un'eccezione voluta al principio «una sola logica».
- **Chi:** Vittorio. **Stato:** Applicata.

#### D-27 – Filtri sul database solo semplici
- **Decisione:** nelle richieste al database si usano solo confronti semplici (es. `id_edizione = …`); condizioni su date, `Coalesce` e combinazioni complesse si applicano dopo, sulla collezione, con `RemoveIf`. Applicato in `btnCaricaImpegni` e `btnInvia`.
- **Motivo:** il filtro `valido_fino >= Today()` mandato al database produceva «AppDataSourceError: problema durante l'elaborazione della risposta del server». Due `Filter` annidati non bastano: Power Apps li unisce e li manda insieme al database.
- **Chi:** Vittorio. **Stato:** Applicata. Da estendere a `btnCalcolaStati` se dovesse dare lo stesso errore.

#### D-28 – Preventivo aperto letto una volta in `gblPreventivo`
- **Decisione:** `scrPreventivo.OnVisible` carica il preventivo in `gblPreventivo`, aggiornata dopo salvataggio, invio e perdita; le proprietà dei controlli (`DefaultSelectedItems`, `Visible`, `Default`, `DisplayMode`) leggono la variabile e non il database. Le collezioni si aggiornano con `RemoveIf` per id e non con `Remove` per record.
- **Motivo:** i `LookUp` sul database nelle proprietà dei controlli girano all'avvio e possono fallire («record non trovato»); `Remove` dava errore quando il record della galleria non coincideva esattamente con quello in collezione.
- **Chi:** Vittorio. **Stato:** Applicata.

### Decisioni del cliente recepite nel back-office (11/11)

Direttore commerciale ruolo distinto; sconto fedeltà 5% anche cambiando stand (prelazione solo sullo stesso stand); preventivo non approvato torna in bozza; chiusura vendite 15 giorni prima dell'apertura come parametro dell'edizione; firma tardiva: acconto e saldo entrambi 15 giorni prima dell'apertura; chi firma dopo il 19/10 non cambia stand e chi firma dopo il 19/09 e disdice deve l'intero importo; Indie Showcase solo P3 dal 2026; promemoria e primo sollecito inviati dalla piattaforma solo per l'edizione in corso.
