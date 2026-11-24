// Riepilogo per padiglione a partire dalla serie dell'API.
//
// IMPORTANTE: qui NON si calcola la stima di presenza. La regola (permanenza, Family, letture
// ripetute) vive solo nel backend, che la restituisce in `presenti_stimati`. Il frontend la
// mostra e basta, così il numero è identico in tutte le viste. Se il campo manca (è facoltativo
// finché l'endpoint è in stato «proposta») la stima risulta non disponibile, non inventata.
export const PADIGLIONI = ['P1', 'P2', 'P3'];

const haValore = (n) => Number.isFinite(n);

export function riepilogaPadiglioni(serie = []) {
  return PADIGLIONI.map((codice) => {
    const punti = serie
      .filter((p) => p.codice_padiglione === codice)
      .sort((a, b) => Date.parse(a.inizio) - Date.parse(b.inizio));

    const ingressiOggi = punti.reduce((totale, p) => totale + (p.ingressi_validi || 0), 0);
    const conStima = punti.filter((p) => haValore(p.presenti_stimati));

    if (conStima.length === 0) {
      return { codice, ingressiOggi, haStima: false, presentiOra: null, picco: null };
    }

    const ultimo = conStima[conStima.length - 1];
    // Picco = valore massimo della stima nella giornata; a parità vale il primo momento.
    const punto = conStima.reduce((max, p) => (p.presenti_stimati > max.presenti_stimati ? p : max), conStima[0]);

    return {
      codice,
      ingressiOggi,
      haStima: true,
      presentiOra: ultimo.presenti_stimati,
      picco: { valore: punto.presenti_stimati, inizio: punto.inizio },
    };
  });
}
