package it.arsdigital.arcadeapi.repository;

import it.arsdigital.arcadeapi.model.Informativa;

import java.util.Optional;

public interface InformativaRepository {

    /** Informativa in vigore oggi, se esiste. */
    Optional<Informativa> leggiInVigore();
}