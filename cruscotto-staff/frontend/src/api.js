// Lettura dell'andamento degli ingressi.
// Endpoint: GET /edizioni/{id_edizione}/ingressi/andamento (ruoli staff e direzione).
const BASE = (import.meta.env.VITE_API_BASE_URL || '/api/v1').replace(/\/$/, '');
const TOKEN = import.meta.env.VITE_API_TOKEN || '';

export const ID_EDIZIONE = Number(import.meta.env.VITE_ID_EDIZIONE || 2026);
export const PERMANENZA_MINUTI = Number(import.meta.env.VITE_PERMANENZA_MINUTI || 120);
export const REFRESH_MS = Number(import.meta.env.VITE_REFRESH_MS || 60000);
const TIMEOUT_MS = 15000;

export class ErroreApi extends Error {
  constructor(messaggio, stato) {
    super(messaggio);
    this.name = 'ErroreApi';
    this.stato = stato;
  }
}

export async function leggiAndamento({ da, signal } = {}) {
  const parametri = new URLSearchParams({
    intervallo: '1m',
    permanenza_minuti: String(PERMANENZA_MINUTI),
  });
  if (da) parametri.set('da', da);

  // Un'unica richiesta non deve bloccare l'aggiornamento: dopo 15 secondi si interrompe.
  const controllo = new AbortController();
  const timer = setTimeout(() => controllo.abort(), TIMEOUT_MS);
  signal?.addEventListener('abort', () => controllo.abort());

  try {
    const risposta = await fetch(`${BASE}/edizioni/${ID_EDIZIONE}/ingressi/andamento?${parametri}`, {
      headers: {
        Accept: 'application/json',
        ...(TOKEN ? { Authorization: `Bearer ${TOKEN}` } : {}),
      },
      signal: controllo.signal,
    });

    if (!risposta.ok) {
      let corpo = null;
      try {
        corpo = await risposta.json();
      } catch {
        /* il corpo dell'errore non è JSON */
      }
      throw new ErroreApi(corpo?.messaggio || `Errore ${risposta.status}`, risposta.status);
    }
    return await risposta.json();
  } finally {
    clearTimeout(timer);
  }
}
