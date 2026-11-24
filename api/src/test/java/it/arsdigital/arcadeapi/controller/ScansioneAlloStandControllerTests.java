package it.arsdigital.arcadeapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScansioneAlloStandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    // Il prefisso e definito in application.properties (server.servlet.context-path).
    // Se viene tolto, il test fallisce perche la proprieta non si trova.
    @Value("${server.servlet.context-path}")
    private String prefisso;

    @Test
    void informativaCorrenteRispondeOk() throws Exception {
        mockMvc.perform(get(prefisso + "/informativa/corrente").contextPath(prefisso))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versione").value("prova-0"));
    }

    @Test
    void operazioneNonScrittaRispondeNonImplementato() throws Exception {
        mockMvc.perform(get(prefisso + "/edizioni/2025/biglietti/esclusioni-contatto").contextPath(prefisso))
                .andExpect(status().isNotImplemented());
    }
}