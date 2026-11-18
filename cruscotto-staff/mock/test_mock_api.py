from datetime import datetime, timedelta, timezone

from mock_api import costruisci_serie, presenti_da_validi

# Dati inventati.


def test_finestra_scorrevole():
    assert presenti_da_validi([1, 2, 3, 4], 2) == [1, 3, 5, 7]


def test_finestra_piu_lunga_della_serie():
    assert presenti_da_validi([1, 2, 3, 4], 15) == [1, 3, 6, 10]


def test_serie_vuota():
    assert presenti_da_validi([], 120) == []


def test_ultimo_punto_uguale_alla_somma_degli_ultimi_minuti():
    a = datetime(2026, 12, 18, 12, 0, tzinfo=timezone.utc)
    da = a - timedelta(minutes=200)
    serie = costruisci_serie(da, a, 120)
    p1 = [p for p in serie if p["codice_padiglione"] == "P1"]
    ultimi = p1[-120:]
    assert p1[-1]["presenti_stimati"] == sum(p["ingressi_validi"] for p in ultimi)


def test_senza_presenti_il_campo_manca():
    a = datetime(2026, 12, 18, 12, 0, tzinfo=timezone.utc)
    serie = costruisci_serie(a - timedelta(minutes=5), a, 120, con_presenti=False)
    assert serie and all("presenti_stimati" not in p for p in serie)


def test_tre_padiglioni_stesso_numero_di_minuti():
    a = datetime(2026, 12, 18, 12, 0, tzinfo=timezone.utc)
    serie = costruisci_serie(a - timedelta(minutes=9), a, 120)
    conteggi = {c: sum(1 for p in serie if p["codice_padiglione"] == c) for c in ("P1", "P2", "P3")}
    assert conteggi == {"P1": 10, "P2": 10, "P3": 10}
