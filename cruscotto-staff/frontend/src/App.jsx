import { useAndamento } from './useAndamento';
import { riepilogaPadiglioni } from './riepilogo';
import { formatOra } from './tempo';
import { PERMANENZA_MINUTI } from './api';
import RigaPadiglione from './components/RigaPadiglione';

export default function App() {
  const { dati, errore, caricamento } = useAndamento();
  const righe = riepilogaPadiglioni(dati?.serie);

  return (
    <main className="schermo">
      <header className="testata">
        <h1>Affluenza nei padiglioni</h1>
        <p className="aggiornamento">Dati aggiornati alle {dati ? formatOra(dati.aggiornato_il) : '—'}</p>
      </header>

      {errore && (
        <p className="avviso" role="alert">
          Aggiornamento non riuscito: {dati ? `i numeri sono fermi alle ${formatOra(dati.aggiornato_il)}. ` : ''}
          Nuovo tentativo tra poco.
        </p>
      )}

      {caricamento && !dati ? (
        <p className="stato">Caricamento dei dati…</p>
      ) : !dati ? (
        <p className="stato">Nessun dato da mostrare. Controlla la connessione con l’API.</p>
      ) : (
        <div className="righe">
          {righe.map((r) => (
            <RigaPadiglione key={r.codice} r={r} />
          ))}
        </div>
      )}

      <footer className="piede">
        Le presenze sono stime: i varchi registrano solo gli ingressi, e si assume una permanenza di{' '}
        {PERMANENZA_MINUTI} minuti. Staff, espositori e fornitori non passano dai varchi.
      </footer>
    </main>
  );
}
