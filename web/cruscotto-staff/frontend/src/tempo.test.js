import { describe, expect, it } from 'vitest';
import { formatOra, inizioGiornataRoma } from './tempo';

describe('tempo', () => {
  it('mezzanotte di Roma in inverno (+01:00)', () => {
    expect(inizioGiornataRoma(new Date('2026-12-18T11:30:00Z'))).toBe('2026-12-18T00:00:00+01:00');
  });

  it('mezzanotte di Roma in estate (+02:00)', () => {
    expect(inizioGiornataRoma(new Date('2026-07-10T10:00:00Z'))).toBe('2026-07-10T00:00:00+02:00');
  });

  it('dopo le 23 UTC Roma è già il giorno dopo', () => {
    expect(inizioGiornataRoma(new Date('2026-12-18T23:30:00Z'))).toBe('2026-12-19T00:00:00+01:00');
  });

  it('mostra sempre l’ora italiana', () => {
    expect(formatOra('2026-12-18T09:03:00+01:00')).toBe('09:03');
    expect(formatOra('2026-12-18T08:03:00Z')).toBe('09:03');
  });

  it('gestisce valori vuoti o non validi', () => {
    expect(formatOra(null)).toBe('—');
    expect(formatOra('non-una-data')).toBe('—');
  });
});
