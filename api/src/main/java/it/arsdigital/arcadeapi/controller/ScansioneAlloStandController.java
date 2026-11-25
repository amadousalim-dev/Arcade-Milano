package it.arsdigital.arcadeapi.controller;

import it.arsdigital.arcadeapi.api.ScansioneAlloStandApi;
import it.arsdigital.arcadeapi.model.EsclusioniContatto;
import it.arsdigital.arcadeapi.model.Informativa;
import it.arsdigital.arcadeapi.model.InvioContatti;
import it.arsdigital.arcadeapi.model.InvioScansioni;
import it.arsdigital.arcadeapi.model.RisultatoContatti;
import it.arsdigital.arcadeapi.model.RisultatoScansioni;
import it.arsdigital.arcadeapi.repository.InformativaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ScansioneAlloStandController implements ScansioneAlloStandApi {

    private final InformativaRepository informativaRepository;

    public ScansioneAlloStandController(InformativaRepository informativaRepository) {
        this.informativaRepository = informativaRepository;
    }

    @Override
    public ResponseEntity<Informativa> leggiInformativaCorrente() {
        return informativaRepository.leggiInVigore()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nessuna informativa in vigore"));
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