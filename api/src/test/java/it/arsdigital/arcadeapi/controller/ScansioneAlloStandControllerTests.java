package it.arsdigital.arcadeapi.controller;

import it.arsdigital.arcadeapi.model.Informativa;
import it.arsdigital.arcadeapi.repository.InformativaRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScansioneAlloStandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InformativaRepository informativaRepository;

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
    void operazioneNonScrittaRispondeNonImplementato() throws Exception {
        mockMvc.perform(get(prefisso + "/edizioni/2025/biglietti/esclusioni-contatto").contextPath(prefisso))
                .andExpect(status().isNotImplemented());
    }
}