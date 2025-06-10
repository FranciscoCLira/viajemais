package com.viajemais.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    @Value("${app.reload-data:false}")
    private boolean reloadData;

    public DataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verifica count de DESTINO e CLIENTE
        Integer countDestino = jdbc.queryForObject("SELECT COUNT(*) FROM DESTINO", Integer.class);
        Integer countCliente = jdbc.queryForObject("SELECT COUNT(*) FROM CLIENTE", Integer.class);

        boolean empty = (countDestino == null || countDestino == 0)
                     && (countCliente == null || countCliente == 0);

        if (reloadData || empty) {
            // Se reloadData, limpa todas as tabelas relacionadas
            if (reloadData) {
                jdbc.execute("DELETE FROM ITEM_CONTRATACAO");
                jdbc.execute("DELETE FROM CONTRATACAO");
                jdbc.execute("DELETE FROM DESTINO");
                jdbc.execute("DELETE FROM CLIENTE");
            }
            // Carrega e executa o data.sql
            ClassPathResource resource = new ClassPathResource("data.sql");
            String sql = StreamUtils.copyToString(
                resource.getInputStream(), StandardCharsets.UTF_8);

            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) {
                    jdbc.execute(s);
                }
            }
        }
    }
}
