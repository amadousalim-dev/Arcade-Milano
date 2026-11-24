import { useEffect, useState } from 'react';
import { leggiAndamento, REFRESH_MS } from './api';
import { inizioGiornataRoma } from './tempo';

// Legge l'andamento subito e poi ogni REFRESH_MS. Se una lettura fallisce tiene gli ultimi dati
// buoni e segnala l'errore; al tentativo successivo riprova da solo.
export function useAndamento(intervalloMs = REFRESH_MS) {
  const [stato, setStato] = useState({ dati: null, errore: null, caricamento: true });

  useEffect(() => {
    let attivo = true;
    let timer;
    const controllo = new AbortController();

    async function ciclo() {
      try {
        const dati = await leggiAndamento({ da: inizioGiornataRoma(), signal: controllo.signal });
        if (!attivo) return;
        setStato({ dati, errore: null, caricamento: false });
      } catch (errore) {
        if (!attivo) return;
        setStato((precedente) => ({ ...precedente, errore, caricamento: false }));
      }
      if (attivo) timer = setTimeout(ciclo, intervalloMs);
    }

    ciclo();
    return () => {
      attivo = false;
      controllo.abort();
      clearTimeout(timer);
    };
  }, [intervalloMs]);

  return stato;
}
