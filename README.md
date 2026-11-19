# GS-Amplia-2026

API RESTful em Java (Spring Boot) para gestao de linhas, estacoes, trens, viagens e bilhetes de trem/metro.
Projeto da Global Solution 2026 (Domain Driven Design - Java).

## Tecnologias
- Java 21, Maven, Spring Boot 3.5
- Acesso ao banco com **JDBC puro** (padrao DAO + BO + Controller)
- Banco de dados **Oracle** (Fiap)

## Estrutura
```
src/main/java/br/com/fiap/trem
  model/        classes de dominio (uma por tabela)
  dao/          acesso ao banco (JDBC)
  bo/           regras de negocio e validacoes
  controller/   endpoints REST (GET, POST, PUT, DELETE)
  exception/    tratamento global de excecoes
  util/         apoio (JDBC e validacao)
database/       scriptDDL.sql (tabelas) e scriptDML.sql (dados de exemplo)
```

## Como executar
1. Rode `database/scriptDDL.sql` e depois `database/scriptDML.sql` no Oracle.
2. Defina as variaveis de ambiente (veja `.env.example`): `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. Execute:
```
mvn spring-boot:run
```
A API sobe em `http://localhost:8080`. Exemplos de chamadas em `requests.http`.

## Endpoints
| Recurso | Caminho | Metodos |
|---|---|---|
| Operadora | `/operadoras` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Estacao | `/estacoes` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Passageiro | `/passageiros` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Linha | `/linhas` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Trem | `/trens` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Viagem | `/viagens` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| Bilhete | `/bilhetes` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| LinhaEstacao | `/linhas-estacoes` | GET, GET /{codigo}, POST, PUT /{codigo}, DELETE /{codigo} |
| TipoModal | `/tipos-modais` | GET, GET /{sigla}, POST, PUT /{sigla}, DELETE /{sigla} |
| SituacaoTrem | `/situacoes-trem` | GET, GET /{sigla}, POST, PUT /{sigla}, DELETE /{sigla} |

## Front-end
Painel web em HTML/CSS/JS puro, em `src/main/resources/static`. O próprio Spring Boot o serve na raiz (`/`), então
o mesmo deploy publica API e tela, sem CORS. Abra `http://localhost:8080/` (ou a URL do Render) e use o mapa ou o menu
para cadastrar, editar e excluir em todas as 10 tabelas.

## Deploy (Render)
Servico Web com **Docker** (usa o `Dockerfile` da raiz). Configure `DB_URL`, `DB_USER` e `DB_PASSWORD` em Environment. Se o `Dockerfile` não estiver na raiz do repositório, defina *Root Directory* como `trem-api`.

#### Links
- **GitHub:** https://github.com/usuario/GS-Amplia-2026
- **Deploy (Render):** https://gs-amplia-api.onrender.com

## Participantes 
1. Erick Munhoes Anciães - 561484
2. João Paulo Fernandes - 563430
3. João Pedro Mendes - 558779
4. Patrick Canuto dos Santos - 563776
5. Rafael Augusto Carmona - 563758
