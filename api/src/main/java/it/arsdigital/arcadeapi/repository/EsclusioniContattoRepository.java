package it.arsdigital.arcadeapi.repository;

import java.time.LocalDate;
import java.util.List;

public interface EsclusioniContattoRepository {

    boolean edizioneEsiste(int idEdizione);

    /** Codici dei biglietti per cui non si propone il contatto. Il filtro per giorno e facoltativo. */
    List<String> codiciDaEscludere(int idEdizione, LocalDate giorno);
}