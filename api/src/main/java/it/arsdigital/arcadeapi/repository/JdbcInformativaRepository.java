package it.arsdigital.arcadeapi.repository;

import it.arsdigital.arcadeapi.model.Informativa;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcInformativaRepository implements InformativaRepository {

    /** Il database lavora in UTC, quindi la data di oggi va calcolata in ora italiana. */
    static final ZoneId FUSO_ITALIA = ZoneId.of("Europe/Rome");

    private final JdbcClient jdbc;

    public JdbcInformativaRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Informativa> leggiInVigore() {
        LocalDate oggi = LocalDate.now(FUSO_ITALIA);
        return jdbc.sql("""
                SELECT id_informativa, versione, testo, in_vigore_dal
                FROM arcade.informativa_privacy
                WHERE in_vigore_dal <= :oggi
                ORDER BY in_vigore_dal DESC, id_informativa DESC
                LIMIT 1
                """)
                .param("oggi", oggi)
                .query((rs, riga) -> {
                    Informativa informativa = new Informativa();
                    informativa.setIdInformativa(rs.getInt("id_informativa"));
                    informativa.setVersione(rs.getString("versione"));
                    informativa.setTesto(rs.getString("testo"));
                    informativa.setInVigoreDal(rs.getObject("in_vigore_dal", LocalDate.class));
                    return informativa;
                })
                .optional();
    }
}