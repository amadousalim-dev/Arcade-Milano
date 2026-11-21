package it.arsdigital.arcadeapi.controller;

import it.arsdigital.arcadeapi.api.ScansioneAlloStandApi;
import it.arsdigital.arcadeapi.model.EsclusioniContatto;
import it.arsdigital.arcadeapi.model.Informativa;
import it.arsdigital.arcadeapi.model.InvioContatti;
import it.arsdigital.arcadeapi.model.InvioScansioni;
import it.arsdigital.arcadeapi.model.RisultatoContatti;
import it.arsdigital.arcadeapi.model.RisultatoScansioni;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScansioneAlloStandController implements ScansioneAlloStandApi {

    // Risposta di prova, senza database. Sara sostituita da una query.
    @Override
    public ResponseEntity<Informativa> leggiInformativaCorrente() {
        Informativa informativa = new Informativa();
        informativa.setIdInformativa(1);
        informativa.setVersione("prova-0");
        informativa.setTesto("Testo di prova dell'informativa.");
        informativa.setInVigoreDal(LocalDate.of(2026, 11, 21));
        return ResponseEntity.ok(informativa);
    }

    @Override
    public ResponseEntity<EsclusioniContatto> leggiEsclusioniContatto(
            Integer idEdizione, OffsetDateTime da, LocalDate giorno) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<RisultatoScansioni> inviaScansioni(InvioScansioni invioScansioni) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<RisultatoContatti> inviaContatti(InvioContatti invioContatti) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}