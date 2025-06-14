-- 1) adiciona coluna permitida null
ALTER TABLE DESTINO ADD COLUMN CATEGORIA_ID BIGINT;

-- 2) insere categoria de fallback se necessário
INSERT INTO CATEGORIA (NOME, SITUACAO, DATA)
  SELECT 'Praia','A', CURRENT_DATE()
  WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE NOME='Praia');

-- 3) preenche o novo campo nos destinos antigos
UPDATE DESTINO d
   SET CATEGORIA_ID = (SELECT ID FROM CATEGORIA WHERE NOME='Praia');

-- 4) bloqueia null e adiciona FK
ALTER TABLE DESTINO ALTER COLUMN CATEGORIA_ID BIGINT NOT NULL;
ALTER TABLE DESTINO
  ADD CONSTRAINT FK_DESTINO_CATEGORIA
  FOREIGN KEY (CATEGORIA_ID)
  REFERENCES CATEGORIA(ID);


INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Praia','A',CURRENT_DATE());
INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Cidade','A',CURRENT_DATE());
INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Parque','A',CURRENT_DATE());
INSERT INTO CATEGORIA (nome, situacao, data) VALUES ('Museu','A',CURRENT_DATE());

INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Praia do Caribe', 'Praias', 'https://magazine.zarpo.com.br/wp-content/uploads/2022/02/capa_praia-do-caribe_zarpo-770x515.jpg', 8500.00);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Fernando de Noronha', 'Praias', 'https://www.melhoresdestinos.com.br/wp-content/uploads/2022/02/fernando-noronha-capa-2022-1536x805.jpg', 6500.01);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Orlando', 'Parques', 'https://example.com/orlando.jpg', 9200.05);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Nova York', 'Cidades', 'https://example.com/ny.jpg', 9900.04);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Rio de Janeiro', 'Cidades', 'https://example.com/rj.jpg', 1900.03);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Fortaleza', 'Cidades', 'https://example.com/Fortaleza.jpg', 5980.05);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Ibirapuera', 'Parques', 'https://grupospimovel.s3.amazonaws.com/Imagens/Noticias/Sistema/mercado-imobiliario/Parque_Ibirapuera.jpg', 1980.00);

INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Fortaleza 2', 'Cidades', 'https://example.com/Fortaleza.jpg', 3980.05);
INSERT INTO destino (local, categoria, imagem_url, preco) VALUES ('Fortaleza 3', 'Cidades', 'https://example.com/Fortaleza.jpg', 1000.01);


INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-01', 'Francisco Casemiro Lira', 'A');    
INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-02', 'Frederico da Silva', 'A');         
INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-03', 'Gloria de Menezes mais um', 'A');  
INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-04', 'Abelarto Chacrinha', 'A');
INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-10', 'Novo Cliente 12', 'I');
INSERT INTO CLIENTE (DATA_CADASTRO, NOME_CLIENTE, SITUACAO_CLIENTE) VALUES (DATE '2025-06-10', 'Novo Cliente Novissimo', 'P');