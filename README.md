#  ViajeMais - Projeto Experimental de viagens de turismo

#### Aplicação Spring Boot para gestão de destinos, clientes e contratações de viagens.

## Principais Funcionalidades
##### Cadastro de Clientes
  - Criar, editar (situação) e excluir (apenas clientes cancelados).   
  - Nome único, validações de formato e tamanho.
  
##### Gestão de Destinos
  - CRUD completo de destinos (Local, Categoria, Preço, URL de imagem).   
  - Preço em formato brasileiro (1.234,56 ou 1234), 
    com validação estrita e valor mínimo de R$ 1,00.
  - Exclusão condicionada a não haver contratacões vinculadas.

    
##### Categorias de Destino
  - CRUD de categorias (nome único, situação, data).    
  - Relação N:1 entre Destino → Categoria.
  - Excluir apenas categorias canceladas.
    
##### Confirmação de Viagem
  - Seleção de destinos, autocomplete de cliente, validações de cliente,
    número de pessoas e período de datas (início ≥ corrente, 
    fim ≥ início, duração ≤ 90 dias).
      
      
##### Histórico de Contratações
  - Cálculo de diárias, preço por diária, valor total (incluindo Qt. Pessoas).
  - Edição de período/quantidade e exclusão apenas para viagens futuras.


#     
## Banco de Dados H2 em disco
  - Persistência em arquivo (jdbc:h2:file:…),
    carregamento inicial via CommandLineRunner com opção de reload.
  - Sequences reiniciáveis para IDs começando em 1.

# 
## Modelo de Dados (ERD)
    Gerado via https://mermaid.live com src/main/resources/static/docs/viajemais.mmd 
![ERD](assets/01A0-Diagrama.png)
##  
![ERD](assets/01A0-Diagrama.jpg)
##  
![ERD](assets/01A0-Diagrama80.png)
 
# 
## Tecnologias

##### Java 17, Spring Boot, Spring Data JPA, Thymeleaf
##### H2 Database, Maven
##### SLF4J + Logback, Bean Validation (JSR-303)

# 
## Como Executar

1. Clonar o repositório
2. git clone https://github.com/FranciscoCLira/viajemais.git
3. cd viajemais
4. Configurar se quiser recarregar dados (application.properties):
5. app.reload-data=true  # ou false para manter dados existentes
6. Construir e iniciar:
7. mvn clean package
8. mvn spring-boot:run
9. Acessar no navegador:
   - Home: http://localhost:8080/   
   - H2 Console: http://localhost:8080/h2-console (URL JDBC em application.properties)

# 
## Páginas web principais e IDE 

##### IDE - restart app localHost 
![IDE-STS4](assets/01A1-IDE-STS-4.png)
  
##### Home e Selecinar Viagens 
![Home](assets/01B1-Home.jpg)
  
##### Confirmação de Viagem 
![ConfirmacaoViagem](assets/01B2-ConfirmacaoViagem.jpg)
  
##### Histórico da Confirmação de Viagem 
![ConfirmacaoViagemHistorico](assets/01B2-ConfirmacaoViagemHistorico.jpg)
  
##### Histórico de Contratações  
![HistoricoContratacoes](assets/01B3-HistoricoContratacoes.jpg)
  
##### Editar Contratação 
![EditarContratacao](assets/01B4-EditarContratacao.jpg)
  
##### Excluir Contratação 
![ExcluirContratacao](assets/01B5-ExcluirContratacao.jpg)
 
##### Categorias
![Categorias](assets/01C1-Categorias.jpg)
 
##### Nova Categoria
![NovaCategoria](assets/01C2-NovaCategoria.jpg)

##### Editar Categorias
![EditarCategorias](assets/01C3-EditarCategoria.jpg)

##### Destinos
![Destinos](assets/01D1-Destinos.jpg))

##### Novo Destino
![NovoDestino](assets/01D2-NovoDestino.jpg)

##### Editar Destino
![EditarDestino](assets/01D3-EditarDestino.jpg)

##### Novo Cliente
![NovoCliente](assets/01E1-NovoCliente.jpg)

##### Clientes Cadastrados 
![Clientes](assets/01E2-ClientesCadastrados.jpg)


# 
## DB H2: 
 
##### H2 DB Console Login 
![H2Login](assets/H2-00-DB-Login.jpg)
  
##### CLIENTE 
![Cliente](assets/H2-01-Cliente.jpg)

##### CATEGORIA 
![Categoria](assets/H2-02-Categoria.jpg)

##### DESTINO  
![Destino](assets/H2-03-Destino.jpg)

##### CONTRATACAO 
![Contratacao](assets/H2-04-Contratacao.jpg)

##### ITEM_CONTRATACAO 
![ItemContratacao](assets/H2-05-ItemContratacao.jpg)
                                                    
                                               
        
__________________________________________________________________________
Este README sintetiza o histórico de implementações e correções do projeto “ViajeMais”, agora pronto para uso e evolução contínua.

# 
## Autor
   Francisco Casemiro Lira
    
   https://www.linkedin.com/in/franciscoclira


