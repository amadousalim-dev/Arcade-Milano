import { describe, expect, it } from 'vitest';
import { riepilogaPadiglioni } from './riepilogo';

// Tutti i numeri di questo file sono inventati.
const punto = (codice, inizio, validi, presenti) => ({
  codice_padiglione: codice,
  inizio,
  ingressi_validi: validi,
  ingressi_rifiutati: 0,
  ...(presenti === undefined ? {} : { presenti_stimati: presenti }),
});

describe('riepilogaPadiglioni', () => {
  it('somma gli ingressi, prende l’ultimo valore come adesso e il massimo come picco', () => {
    const serie = [
      punto('P1', '2026-12-18T09:02:00+01:00', 5, 17),
      punto('P1', '2026-12-18T09:00:00+01:00', 4, 4), // fuori ordine di proposito
      punto('P1', '2026-12-18T09:01:00+01:00', 8, 12),
      punto('P2', '2026-12-18T09:00:00+01:00', 2, 2),
    ];
    const [p1, p2, p3] = riepilogaPadiglioni(serie);
    expect(p1).toMatchObject({ codice: 'P1', ingressiOggi: 17, haStima: true, presentiOra: 17 });
    expect(p1.picco).toEqual({ valore: 17, inizio: '2026-12-18T09:02:00+01:00' });
    expect(p2).toMatchObject({ codice: 'P2', ingressiOggi: 2, presentiOra: 2 });
    expect(p3).toMatchObject({ codice: 'P3', ingressiOggi: 0, haStima: false });
  });

  it('il picco può essere anteriore al valore attuale', () => {
    const serie = [
      punto('P1', '2026-12-18T10:00:00+01:00', 9, 30),
      punto('P1', '2026-12-18T10:01:00+01:00', 1, 20),
    ];
    const [p1] = riepilogaPadiglioni(serie);
    expect(p1.presentiOra).toBe(20);
    expect(p1.picco).toEqual({ valore: 30, inizio: '2026-12-18T10:00:00+01:00' });
  });

  it('a parità di valore il picco è il primo momento', () => {
    const serie = [
      punto('P3', '2026-12-18T10:00:00+01:00', 3, 10),
      punto('P3', '2026-12-18T10:01:00+01:00', 3, 10),
    ];
    expect(riepilogaPadiglioni(serie)[2].picco.inizio).toBe('2026-12-18T10:00:00+01:00');
  });

  it('senza presenti_stimati mostra solo gli ingressi e non inventa la stima', () => {
    const serie = [punto('P1', '2026-12-18T09:00:00+01:00', 6)];
    const [p1] = riepilogaPadiglioni(serie);
    expect(p1).toEqual({ codice: 'P1', ingressiOggi: 6, haStima: false, presentiOra: null, picco: null });
  });

  it('tratta null come stima mancante e usa gli altri punti', () => {
    const serie = [
      punto('P2', '2026-12-18T09:00:00+01:00', 1, 7),
      { ...punto('P2', '2026-12-18T09:01:00+01:00', 1), presenti_stimati: null },
    ];
    const [, p2] = riepilogaPadiglioni(serie);
    expect(p2).toMatchObject({ haStima: true, presentiOra: 7, ingressiOggi: 2 });
  });

  it('con serie vuota o assente restituisce tre padiglioni a zero', () => {
    for (const serie of [[], undefined]) {
      const righe = riepilogaPadiglioni(serie);
      expect(righe.map((r) => r.codice)).toEqual(['P1', 'P2', 'P3']);
      expect(righe.every((r) => r.ingressiOggi === 0 && !r.haStima)).toBe(true);
    }
  });

  it('una stima pari a zero è un valore valido, non mancante', () => {
    const [p1] = riepilogaPadiglioni([punto('P1', '2026-12-18T09:00:00+01:00', 0, 0)]);
    expect(p1).toMatchObject({ haStima: true, presentiOra: 0 });
    expect(p1.picco.valore).toBe(0);
  });
});
