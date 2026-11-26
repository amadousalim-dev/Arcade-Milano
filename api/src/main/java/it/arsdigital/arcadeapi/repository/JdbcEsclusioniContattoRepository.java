package it.arsdigital.arcadeapi.repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEsclusioniContattoRepository implements EsclusioniContattoRepository {

    // Regola del contratto: fasce minorili e tipi ridotto o gratuito per bambini.
    // Scelta prudente B-02, da confermare con il cliente: anche i biglietti family.
    private static final String CONDIZIONE_ESCLUSIONE =
            "(fascia_eta IN ('0-7', '8-13', '14-17') "
            + "OR tipo_biglietto IN ('ridotto', 'gratuito under 8', 'family'))";

    private final JdbcClient jdbc;

    public JdbcEsclusioniContattoRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean edizioneEsiste(int idEdizione) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM arcade.edizione WHERE id_edizione = :id)")
                .param("id", idEdizione)
                .query(Boolean.class)
                .single();
    }

    @Override
    public List<String> codiciDaEscludere(int idEdizione, LocalDate giorno) {
        // Le parti aggiunte al testo sono solo pezzi fissi scritti da noi:
        // i valori arrivano sempre come parametri.
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id_biglietto FROM arcade.biglietto ");
        sql.append("WHERE id_edizione = :idEdizione ");
        sql.append("AND ").append(CONDIZIONE_ESCLUSIONE);

        Map<String, Object> parametri = new HashMap<>();
        parametri.put("idEdizione", idEdizione);

        if (giorno != null) {
            sql.append(" AND (giorno_valido IS NULL OR giorno_valido = :giorno)");
            parametri.put("giorno", giorno);
        }
        sql.append(" ORDER BY id_biglietto");

        return jdbc.sql(sql.toString())
                .params(parametri)
                .query(String.class)
                .list();
    }
}