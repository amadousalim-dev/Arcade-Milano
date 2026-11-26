package it.arsdigital.arcadeapi.controller;

import it.arsdigital.arcadeapi.model.Informativa;
import it.arsdigital.arcadeapi.repository.EsclusioniContattoRepository;
import it.arsdigital.arcadeapi.repository.InformativaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScansioneAlloStandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InformativaRepository informativaRepository;

    @MockitoBean
    private EsclusioniContattoRepository esclusioniRepository;

    @Value("${server.servlet.context-path}")
    private String prefisso;

    @Test
    void informativaCorrenteRispondeOk() throws Exception {
        Informativa informativa = new Informativa();
        informativa.setIdInformativa(1);
        informativa.setVersione("v-test");
        informativa.setTesto("Testo di prova.");
        informativa.setInVigoreDal(LocalDate.of(2026, 1, 1));
        when(informativaRepository.leggiInVigore()).thenReturn(Optional.of(informativa));

        mockMvc.perform(get(prefisso + "/informativa/corrente").contextPath(prefisso))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versione").value("v-test"));
    }

    @Test
    void informativaAssenteRispondeNonTrovato() throws Exception {
        when(informativaRepository.leggiInVigore()).thenReturn(Optional.empty());

        mockMvc.perform(get(prefisso + "/informativa/corrente").contextPath(prefisso))
                .andExpect(status().isNotFound());
    }

    @Test
    void esclusioniRestituisceICodici() throws Exception {
        when(esclusioniRepository.edizioneEsiste(2025)).thenReturn(true);
        when(esclusioniRepository.codiciDaEscludere(eq(2025), isNull()))
                .thenReturn(List.of("T1", "T2"));

        mockMvc.perform(get(prefisso + "/edizioni/2025/biglietti/esclusioni-contatto").contextPath(prefisso))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_edizione").value(2025))
                .andExpect(jsonPath("$.totale").value(2))
                .andExpect(jsonPath("$.codici[0]").value("T1"));
    }

    @Test
    void esclusioniEdizioneInesistenteRispondeNonTrovato() throws Exception {
        when(esclusioniRepository.edizioneEsiste(2030)).thenReturn(false);

        mockMvc.perform(get(prefisso + "/edizioni/2030/biglietti/esclusioni-contatto").contextPath(prefisso))
                .andExpect(status().isNotFound());
    }

    @Test
    void esclusioniPassaIlGiornoAllaQuery() throws Exception {
        when(esclusioniRepository.edizioneEsiste(2025)).thenReturn(true);

        mockMvc.perform(get(prefisso + "/edizioni/2025/biglietti/esclusioni-contatto")
                        .contextPath(prefisso).param("giorno", "2025-12-20"))
                .andExpect(status().isOk());

        verify(esclusioniRepository).codiciDaEscludere(eq(2025), eq(LocalDate.of(2025, 12, 20)));
    }

    @Test
    void scansioniSenzaElencoRispondeRichiestaNonValida() throws Exception {
        mockMvc.perform(post(prefisso + "/scansioni").contextPath(prefisso)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}