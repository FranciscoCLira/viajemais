package com.viajemais.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    public DataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem registros em DESTINO ou CLIENTE
        Integer countDestino = jdbc.queryForObject("SELECT COUNT(*) FROM DESTINO", Integer.class);
        Integer countCliente = jdbc.queryForObject("SELECT COUNT(*) FROM CLIENTE", Integer.class);

        // Só carrega se ambas as tabelas estiverem vazias
        if ((countDestino == null || countDestino == 0) &&
            (countCliente == null || countCliente == 0)) {

            ClassPathResource resource = new ClassPathResource("data.sql");
            String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) {
                    jdbc.execute(s);
                }
            }
        }
    }
}
