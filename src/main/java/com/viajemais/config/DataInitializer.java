package com.viajemais.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    	
        if (reloadData) {
            // fecha conexões e deleta o arquivo em disco
            Files.deleteIfExists(Paths.get("data/viajemais-db.mv.db"));
            Files.deleteIfExists(Paths.get("data/viajemais-db.trace.db"));
        }
    	
    	// Verifica se precisa recarregar
        // Carga inicial: Conta DESTINO, CLIENTE, CONTRATACAO e ITEM_CONTRATACAO
    	// application.properties: app.reload-data=true - carga / =false nada acontece  
        Integer cntDest = jdbc.queryForObject("SELECT COUNT(*) FROM DESTINO", Integer.class);
        Integer cntCli  = jdbc.queryForObject("SELECT COUNT(*) FROM CLIENTE", Integer.class);
        Integer cntCon  = jdbc.queryForObject("SELECT COUNT(*) FROM CONTRATACAO", Integer.class);
        Integer cntItem = jdbc.queryForObject("SELECT COUNT(*) FROM ITEM_CONTRATACAO", Integer.class);
    	
        // Todas as tabela com dados impede o reload 
        // boolean empty = (cntDest == 0 && cntCli == 0 && cntCon == 0 && cntItem == 0);
        // if (reloadData || empty) {
        
        // Qualquer tabela com dados impede o reload 
        boolean anyData = (cntDest>0) || (cntCli>0) || (cntCon>0) || (cntItem>0);
    	
    	// Log o flag e counts - ver console ao subir a app
    	// System.out.printf("reloadData=%b, counts: DEST=%d, CLI=%d, CON=%d, ITEM=%d%n",
    	// 	    reloadData, cntDest, cntCli, cntCon, cntItem);
    	System.out.println("************************************************************************************");
    	System.out.printf("reloadData=%b, anyData=%b, counts: DESTINO=%d, CLIENTE=%d, CONTRATACAO=%d, ITEM=%d%n",
    		               reloadData, anyData, cntDest, cntCli, cntCon, cntItem);
        
    	// só recarrega se explicitamente pediu (reloadData==true)
    	// e não há NENHUMA tabela com dados (anyData==false)
        if (reloadData || !anyData) {
        	System.out.println("**** Recarregando a basa de dados *******");
        	System.out.println("************************************************************************************");
  
        	// reload só quando for a primeira carga OU reloadData=true
        
        	// 1) Limpa todas as tabelas na ordem de dependência
            jdbc.execute("DELETE FROM ITEM_CONTRATACAO");
            jdbc.execute("DELETE FROM CONTRATACAO");
            jdbc.execute("DELETE FROM DESTINO");
            jdbc.execute("DELETE FROM CLIENTE");

            // 3) Reinicia a sequência do Hibernate (usada por GenerationType.AUTO/SEQUENCE)
            jdbc.execute("ALTER SEQUENCE IF EXISTS hibernate_sequence RESTART WITH 1");

            // 2) Reinicia o IDENTITY de cada tabela — importantes ITEM e CONTRATAÇÃO!
            jdbc.execute("ALTER TABLE ITEM_CONTRATACAO ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE CONTRATACAO      ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE DESTINO          ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE CLIENTE          ALTER COLUMN ID RESTART WITH 1");
            

            // 4) Carrega o data.sql
            String sql = StreamUtils.copyToString(
                new ClassPathResource("data.sql").getInputStream(),
                StandardCharsets.UTF_8
            );
            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) jdbc.execute(s);
            }

            // 5) Sincroniza cod_cliente = id
            jdbc.execute("UPDATE CLIENTE SET cod_cliente = id");
        } else
    	System.out.println("**** Não recarregou a basa de dados *********");
    	System.out.println("************************************************************************************");
    }
}
