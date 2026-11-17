import { formatOra } from '../tempo';

const numero = new Intl.NumberFormat('it-IT');

// Una riga per padiglione: il numero grande è la stima di presenza adesso.
export default function RigaPadiglione({ r }) {
  const titolo = `titolo-${r.codice}`;
  const percentuale =
    r.haStima && r.picco.valore > 0 ? Math.min(100, Math.round((r.presentiOra / r.picco.valore) * 100)) : null;

  return (
    <section className="riga" aria-labelledby={titolo}>
      <h2 id={titolo} className="codice">
        {r.codice}
      </h2>

      <div className="ora">
        <p className="etichetta">Presenti stimati adesso</p>
        {r.haStima ? (
          <p className="valore">{numero.format(r.presentiOra)}</p>
        ) : (
          <p className="valore valore-assente" aria-label="Stima non disponibile">
            —
          </p>
        )}
        {percentuale !== null && (
          <div className="confronto">
            <div
              className="barra"
              role="progressbar"
              aria-label={`Presenza attuale rispetto al picco, ${r.codice}`}
              aria-valuemin={0}
              aria-valuemax={100}
              aria-valuenow={percentuale}
            >
              <div className="barra-piena" style={{ width: `${percentuale}%` }} />
            </div>
            <p className="percentuale">{percentuale}% del picco di oggi</p>
          </div>
        )}
        {!r.haStima && <p className="nota">Stima non disponibile: sono mostrati solo gli ingressi.</p>}
      </div>

      <dl className="dettagli">
        <div>
          <dt>Picco stimato oggi</dt>
          <dd>
            {r.picco ? (
              <>
                {numero.format(r.picco.valore)} <span className="alle">alle {formatOra(r.picco.inizio)}</span>
              </>
            ) : (
              '—'
            )}
          </dd>
        </div>
        <div>
          <dt>Ingressi registrati oggi</dt>
          <dd>{numero.format(r.ingressiOggi)}</dd>
        </div>
      </dl>
    </section>
  );
}
