package it.arsdigital.arcadeapi.repository;

import it.arsdigital.arcadeapi.model.Informativa;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcInformativaRepository implements InformativaRepository {

    private final JdbcClient jdbc;

    public JdbcInformativaRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Informativa> leggiInVigore() {
        return jdbc.sql("""
                SELECT id_informativa, versione, testo, in_vigore_dal
                FROM arcade.informativa_privacy
                WHERE in_vigore_dal <= CURRENT_DATE
                ORDER BY in_vigore_dal DESC
                LIMIT 1
                """)
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