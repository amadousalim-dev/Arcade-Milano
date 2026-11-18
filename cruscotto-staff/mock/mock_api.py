#!/usr/bin/env python3
"""Mock dell'API ARCADE Milano per il cruscotto staff.

TUTTI I DATI SONO INVENTATI. Riproduce la forma di AndamentoIngressi per
GET /v1/edizioni/{id_edizione}/ingressi/andamento (solo intervallo=1m).

La stima `presenti_stimati` è calcolata qui SOLO per far funzionare il mock; la regola vera
(permanenza, Family, letture ripetute) vive nel backend Java.

Uso:
    python mock_api.py                    # porta 8001
    python mock_api.py --senza-presenti   # simula l'API senza il campo facoltativo
"""
import argparse
import json
import math
import random
import re
from datetime import datetime, timedelta, timezone
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs, urlparse
from zoneinfo import ZoneInfo

ROMA = ZoneInfo("Europe/Rome")
# Ingressi medi al minuto per padiglione: numeri inventati.
PADIGLIONI = {"P1": 9.0, "P2": 3.0, "P3": 2.0}
PERMANENZA_DEFAULT, PERMANENZA_MIN, PERMANENZA_MAX = 120, 15, 480
PERCORSO = re.compile(r"^/v1/edizioni/(\d+)/ingressi/andamento$")
CON_PRESENTI = True


def ingressi_al_minuto(codice, istante_utc):
    """Ingressi validi e rifiutati inventati, ripetibili per lo stesso minuto."""
    rng = random.Random(f"{codice}|{int(istante_utc.timestamp()) // 60}")
    ora = istante_utc.astimezone(ROMA)
    ora_decimale = ora.hour + ora.minute / 60
    forma = max(0.1, math.sin(math.pi * (ora_decimale - 6) / 12))  # più intenso a metà giornata
    media = PADIGLIONI[codice] * forma
    validi = max(0, round(rng.gauss(media, math.sqrt(media))))
    rifiutati = 1 if rng.random() < 0.03 else 0
    return validi, rifiutati


def presenti_da_validi(validi, permanenza_min):
    """Per ogni minuto: somma degli ingressi validi degli ultimi `permanenza_min` minuti
    (minuto corrente incluso). Con intervallo di 1 minuto, una finestra = N elementi."""
    totali, corrente = [], 0
    for i, v in enumerate(validi):
        corrente += v
        if i >= permanenza_min:
            corrente -= validi[i - permanenza_min]
        totali.append(corrente)
    return totali


def costruisci_serie(da_utc, a_utc, permanenza_min, con_presenti=True):
    minuti = []
    t = da_utc.replace(second=0, microsecond=0)
    while t <= a_utc:
        minuti.append(t)
        t += timedelta(minutes=1)

    serie = []
    for codice in PADIGLIONI:
        letture = [ingressi_al_minuto(codice, t) for t in minuti]
        presenti = presenti_da_validi([v for v, _ in letture], permanenza_min)
        for t, (validi, rifiutati), p in zip(minuti, letture, presenti):
            punto = {
                "codice_padiglione": codice,
                "inizio": t.astimezone(ROMA).isoformat(timespec="seconds"),
                "ingressi_validi": validi,
                "ingressi_rifiutati": rifiutati,
            }
            if con_presenti:
                punto["presenti_stimati"] = p
            serie.append(punto)
    serie.sort(key=lambda p: (p["inizio"], p["codice_padiglione"]))
    return serie


def errore(codice, messaggio):
    return {"codice": codice, "messaggio": messaggio}


class Gestore(BaseHTTPRequestHandler):
    def _rispondi(self, stato, corpo):
        dati = json.dumps(corpo, ensure_ascii=False).encode("utf-8")
        self.send_response(stato)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Content-Length", str(len(dati)))
        self.end_headers()
        self.wfile.write(dati)

    def do_GET(self):
        url = urlparse(self.path)
        trovato = PERCORSO.match(url.path)
        if not trovato:
            return self._rispondi(404, errore("NON_TROVATO", "Percorso sconosciuto."))
        q = {k: v[0] for k, v in parse_qs(url.query).items()}

        if q.get("intervallo", "1m") != "1m":
            return self._rispondi(400, errore("RICHIESTA_NON_VALIDA", "Il mock supporta solo intervallo=1m."))
        try:
            permanenza = int(q.get("permanenza_minuti", PERMANENZA_DEFAULT))
        except ValueError:
            permanenza = -1
        if not PERMANENZA_MIN <= permanenza <= PERMANENZA_MAX:
            return self._rispondi(
                400,
                errore("RICHIESTA_NON_VALIDA", f"permanenza_minuti deve essere tra {PERMANENZA_MIN} e {PERMANENZA_MAX}."),
            )

        adesso = datetime.now(timezone.utc).replace(second=0, microsecond=0)
        try:
            da = datetime.fromisoformat(q["da"].replace("Z", "+00:00")) if "da" in q else \
                datetime.now(ROMA).replace(hour=0, minute=0, second=0, microsecond=0)
            a = datetime.fromisoformat(q["a"].replace("Z", "+00:00")) if "a" in q else adesso
        except ValueError:
            return self._rispondi(400, errore("RICHIESTA_NON_VALIDA", "Date non valide (formato ISO 8601)."))
        if da.tzinfo is None or a.tzinfo is None:
            return self._rispondi(400, errore("RICHIESTA_NON_VALIDA", "Le date devono avere il fuso orario."))
        da, a = da.astimezone(timezone.utc), min(a.astimezone(timezone.utc), adesso)
        if da > a or (a - da) > timedelta(days=2):
            return self._rispondi(400, errore("RICHIESTA_NON_VALIDA", "Intervallo di date non valido (massimo 2 giorni)."))

        self._rispondi(200, {
            "id_edizione": int(trovato.group(1)),
            "aggiornato_il": adesso.astimezone(ROMA).isoformat(timespec="seconds"),
            "intervallo": "1m",
            "serie": costruisci_serie(da, a, permanenza, CON_PRESENTI),
            "non_riconosciuti": 0,
        })

    def log_message(self, formato, *args):
        print(f"[mock] {self.address_string()} {formato % args}")


def main():
    global CON_PRESENTI
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--porta", type=int, default=8001)
    parser.add_argument("--senza-presenti", action="store_true", help="non include presenti_stimati")
    argomenti = parser.parse_args()
    CON_PRESENTI = not argomenti.senza_presenti
    print(f"Mock con DATI INVENTATI su http://localhost:{argomenti.porta} (presenti_stimati: {CON_PRESENTI})")
    ThreadingHTTPServer(("127.0.0.1", argomenti.porta), Gestore).serve_forever()


if __name__ == "__main__":
    main()
