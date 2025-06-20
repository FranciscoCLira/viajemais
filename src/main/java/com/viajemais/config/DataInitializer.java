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
        Integer cntCatg = jdbc.queryForObject("SELECT COUNT(*) FROM CATEGORIA", Integer.class);
        Integer cntDest = jdbc.queryForObject("SELECT COUNT(*) FROM DESTINO", Integer.class);
        Integer cntCli  = jdbc.queryForObject("SELECT COUNT(*) FROM CLIENTE", Integer.class);
        Integer cntCon  = jdbc.queryForObject("SELECT COUNT(*) FROM CONTRATACAO", Integer.class);
        Integer cntItem = jdbc.queryForObject("SELECT COUNT(*) FROM ITEM_CONTRATACAO", Integer.class);
        
        // Qualquer tabela com dados impede o reload 
        boolean anyData = (cntCatg>0) || (cntDest>0) || (cntCli>0) || (cntCon>0) || (cntItem>0);
    	
    	// Log o flag e counts - ver console ao subir a app
    	// System.out.printf("reloadData=%b, counts: DEST=%d, CLI=%d, CON=%d, ITEM=%d%n",
    	// 	    reloadData, cntDest, cntCli, cntCon, cntItem);
        
    	System.out.println("************************************************************************************");
        System.out.println(".../viajemais/src/main/java/com/viajemais/config/DataInitializer.java:");
    	System.out.printf("reloadData=%b, anyData=%b, counts: CATEGORIA=%d,  DESTINO=%d, CLIENTE=%d, CONTRATACAO=%d, ITEM=%d%n",
    		               reloadData, anyData, cntCatg,  cntDest, cntCli, cntCon, cntItem);
        
    	// só recarrega se explicitamente pediu (reloadData==true)
    	// e não há NENHUMA tabela com dados (anyData==false)
        if (reloadData || !anyData) {
  
        	// reload só quando for a primeira carga OU reloadData=true
        
        	// 1) Limpa todas as tabelas na ordem de dependência
            jdbc.execute("DELETE FROM ITEM_CONTRATACAO");
            jdbc.execute("DELETE FROM CONTRATACAO");
            jdbc.execute("DELETE FROM CATEGORIA");
            jdbc.execute("DELETE FROM DESTINO");
            jdbc.execute("DELETE FROM CLIENTE");

            // 3) Reinicia a sequência do Hibernate (usada por GenerationType.AUTO/SEQUENCE)
            jdbc.execute("ALTER SEQUENCE IF EXISTS hibernate_sequence RESTART WITH 1");

            // 2) Reinicia o IDENTITY de cada tabela — importantes ITEM e CONTRATAÇÃO!
            jdbc.execute("ALTER TABLE ITEM_CONTRATACAO ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE CONTRATACAO      ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE CATEGORIA        ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE DESTINO          ALTER COLUMN ID RESTART WITH 1");
            jdbc.execute("ALTER TABLE CLIENTE          ALTER COLUMN ID RESTART WITH 1");
            
            // 3) Carrega Categorias 
        //  jdbc.execute("INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Praia','A',CURRENT_DATE())");
        //  jdbc.execute("INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Cidade','A',CURRENT_DATE())");
        //  jdbc.execute("INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Parque','A',CURRENT_DATE())");
        //  jdbc.execute("INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Museu','A',CURRENT_DATE())");

            // 4) Carrega o data.sql
            String sql = StreamUtils.copyToString(
                new ClassPathResource("data.sql").getInputStream(),
                StandardCharsets.UTF_8
            );
            for (String stmt : sql.split(";")) {
                String s = stmt.trim();
                if (!s.isEmpty()) jdbc.execute(s);
            }

         // 1) Se ainda existir coluna antiga `CATEGORIA` com o nome em string, atualize o FK:
            jdbc.execute(
              "UPDATE DESTINO " +
              "   SET categoria_id = (SELECT id FROM CATEGORIA WHERE nome = DESTINO.CATEGORIA) " +
              " WHERE categoria_id IS NULL"
            );

            // 2) Marque a coluna como NOT NULL e crie a FK de verdade
            jdbc.execute("ALTER TABLE DESTINO ALTER COLUMN categoria_id BIGINT NOT NULL");
            jdbc.execute(
              "ALTER TABLE DESTINO " +
              "  ADD CONSTRAINT FK_DESTINO_CATEGORIA " +
              "  FOREIGN KEY(categoria_id) REFERENCES CATEGORIA(id)"
            );
            
            // 5) Sincroniza cod_cliente = id
            jdbc.execute("UPDATE CLIENTE SET cod_cliente = id");
            
        	System.out.println("**** Recarregou a basa de dados *******");
        	System.out.println("************************************************************************************");
        } else
    	System.out.println("**** Não recarregou a basa de dados *********");
    	System.out.println("************************************************************************************");
    }
}
