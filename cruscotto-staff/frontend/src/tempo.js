// Date e orari: l'API restituisce l'ora italiana (Europe/Rome). Qui si mostra sempre quella,
// qualunque sia il fuso del computer collegato allo schermo.
const FUSO = 'Europe/Rome';

const formatoOra = new Intl.DateTimeFormat('it-IT', {
  timeZone: FUSO,
  hour: '2-digit',
  minute: '2-digit',
  hourCycle: 'h23',
});

export function formatOra(iso) {
  if (!iso) return '—';
  const data = new Date(iso);
  return Number.isNaN(data.getTime()) ? '—' : formatoOra.format(data);
}

function partiRoma(data) {
  const parti = new Intl.DateTimeFormat('en-CA', {
    timeZone: FUSO,
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hourCycle: 'h23',
  }).formatToParts(data);
  return Object.fromEntries(parti.filter((p) => p.type !== 'literal').map((p) => [p.type, p.value]));
}

// Mezzanotte di oggi a Roma, in formato ISO con fuso (per esempio 2026-12-18T00:00:00+01:00).
// L'offset è quello del momento attuale: nei giorni della fiera (dicembre) coincide con quello
// di mezzanotte.
export function inizioGiornataRoma(adesso = new Date()) {
  const p = partiRoma(adesso);
  const localeComeUtc = Date.UTC(+p.year, +p.month - 1, +p.day, +p.hour, +p.minute, +p.second);
  const offsetMinuti = Math.round((localeComeUtc - Math.floor(adesso.getTime() / 1000) * 1000) / 60000);
  const segno = offsetMinuti >= 0 ? '+' : '-';
  const assoluto = Math.abs(offsetMinuti);
  const hh = String(Math.floor(assoluto / 60)).padStart(2, '0');
  const mm = String(assoluto % 60).padStart(2, '0');
  return `${p.year}-${p.month}-${p.day}T00:00:00${segno}${hh}:${mm}`;
}
