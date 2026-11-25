package it.arsdigital.arcadeapi.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface EsclusioniContattoRepository {

    boolean edizioneEsiste(int idEdizione);

    /** Codici dei biglietti per cui non si propone il contatto. I filtri da e giorno sono facoltativi. */
    List<String> codiciDaEscludere(int idEdizione, OffsetDateTime da, LocalDate giorno);
}