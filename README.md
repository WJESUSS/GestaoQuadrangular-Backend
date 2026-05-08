# ⛪ GestaoQuadrangular — Backend

> Sistema de Gestão para Igreja Quadrangular em Células — API REST com autenticação JWT, geração de relatórios PDF e painel pastoral completo.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Deploy](https://img.shields.io/badge/Deploy-Render-46E3B7?logo=render)](https://render.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

---

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Rodando Localmente](#-rodando-localmente)
- [Rodando com Docker](#-rodando-com-docker)
- [Deploy no Render](#-deploy-no-render)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Endpoints da API](#-endpoints-da-api)
- [Autenticação](#-autenticação)
- [Estrutura de Pastas](#-estrutura-de-pastas)

---

## 📖 Sobre o Projeto

O **GestaoQuadrangular** é um sistema completo de gestão eclesiástica desenvolvido para igrejas que operam no modelo de células. Ele centraliza o controle de membros, células, Casas de Paz, encontros, discipulado, tesouraria e dashboard pastoral em uma API REST robusta e segura.

O sistema está hospedado no **Render** e utiliza **PostgreSQL** como banco de dados relacional.

---

## ✅ Funcionalidades

| Módulo | Descrição |
|---|---|
| 🔐 **Autenticação** | Login com JWT, controle de acesso por perfil |
| 👥 **Membros** | Cadastro, transferência, histórico e status de membros |
| 🏠 **Células** | Criação, vinculação de membros, alertas e ranking |
| 🕊️ **Casa de Paz** | Gestão de Casas de Paz e geração de relatórios em PDF |
| 📋 **Encontros** | Ficha de encontros, presença e relatórios |
| 📈 **Discipulado** | Acompanhamento semanal e relatório de discipulado |
| 🏆 **Ranking** | Ranking de células por desempenho |
| 🔔 **Notificações** | Sistema de alertas para líderes e pastores |
| 🎂 **Aniversariantes** | Listagem de aniversariantes do mês |
| 💰 **Tesouraria** | Lançamentos financeiros de células |
| 📊 **Dashboard Pastoral** | Métricas consolidadas para o pastor |
| 📄 **Relatórios PDF** | Geração de relatórios exportáveis em PDF |
| 📅 **Eventos** | Cadastro e gestão de eventos da igreja |
| 👤 **Visitantes** | Controle de visitantes e decisões de fé |

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 3.5.8**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Actuator
  - Spring Mail
- **PostgreSQL** — banco de dados relacional
- **JWT (JJWT 0.12.5)** — autenticação stateless
- **ModelMapper 3.2.0** — mapeamento de DTOs
- **iText PDF 7.2.5 + OpenPDF 1.3.30** — geração de relatórios PDF
- **Lombok** — redução de boilerplate
- **SpringDoc OpenAPI 2.5.0** — documentação Swagger
- **Docker** — containerização com build multi-stage
- **Render** — plataforma de hospedagem em nuvem

---

## 🏛️ Arquitetura

O projeto segue uma arquitetura em camadas inspirada no **Domain-Driven Design (DDD)**:

```
src/main/java/com/gestaoigrejaemcelula/demo/
│
├── domain/
│   ├── entity/          # Entidades JPA (Membro, Celula, Usuario, etc.)
│   └── repository/      # Interfaces Spring Data JPA
│
├── aplication/
│   ├── dto/             # Data Transfer Objects (Request e Response)
│   └── service/         # Regras de negócio
│
├── web/
│   └── controller/      # Controllers REST
│
└── security/
    └── config/          # Configuração JWT e Spring Security
```

---

## 📦 Pré-requisitos

- [Java 21+](https://adoptium.net/)
- [Maven 3.9+](https://maven.apache.org/)
- [PostgreSQL 14+](https://www.postgresql.org/)
- [Docker](https://www.docker.com/) *(opcional)*

---

## ⚙️ Configuração do Ambiente

Crie um banco de dados PostgreSQL local:

```sql
CREATE DATABASE gestao_quadrangular;
```

Copie o arquivo de propriedades e ajuste as variáveis:

```bash
cp src/main/resources/application.properties src/main/resources/application-local.properties
```

Configure as variáveis no `application.properties` ou via variáveis de ambiente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gestao_quadrangular
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
jwt.secret=sua_chave_secreta
```

---

## ▶️ Rodando Localmente

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/GestaoQuadrangular-Backend.git
cd GestaoQuadrangular-Backend

# Compile e rode
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🐳 Rodando com Docker

O projeto possui um `Dockerfile` com build multi-stage otimizado para produção.

```bash
# Build da imagem
docker build -t gestao-quadrangular-backend .

# Rodar o container
docker run -p 8080:8080 \
  -e DB_HOST=seu_host \
  -e DB_PORT=5432 \
  -e DB_NAME=gestao_quadrangular \
  -e DB_USER=seu_usuario \
  -e DB_PASSWORD=sua_senha \
  -e JWT_SECRET=sua_chave_secreta \
  gestao-quadrangular-backend
```

Ou via Docker Compose:

```bash
docker-compose up -d
```

---

## 🚀 Deploy no Render

O projeto está configurado para deploy contínuo no **Render** com as seguintes especificações:

- **Tipo:** Web Service
- **Runtime:** Docker
- **Porta exposta:** `10000`
- **JVM otimizada para plano free:** `-Xmx384m -Xms64m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m`

### Configuração no Render

1. Crie um novo **Web Service** apontando para o repositório
2. Selecione **Docker** como ambiente de build
3. Configure as [variáveis de ambiente](#-variáveis-de-ambiente) no painel do Render
4. O Render irá fazer o build e deploy automaticamente a cada push na branch `main`

### Health Check

O endpoint de health check utilizado pelo Render é:

```
GET /actuator/health
```

---

## 🔑 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_HOST` | Host do banco de dados | `localhost` |
| `DB_PORT` | Porta do banco de dados | `5432` |
| `DB_NAME` | Nome do banco de dados | `gestao_quadrangular` |
| `DB_USER` | Usuário do banco de dados | — |
| `DB_PASSWORD` | Senha do banco de dados | — |
| `JWT_SECRET` | Chave secreta para geração de tokens JWT | — |
| `JWT_EXPIRATION` | Expiração do token em milissegundos | `86400000` (24h) |
| `PORT` | Porta do servidor | `8080` |

> ⚠️ **Nunca** suba as variáveis sensíveis para o repositório. Utilize sempre variáveis de ambiente ou um gerenciador de segredos.

---

## 🌐 Endpoints da API

### Autenticação
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Login e geração do token JWT |

### Membros
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/membros` | Listar todos os membros |
| `POST` | `/membros` | Cadastrar novo membro |
| `GET` | `/membros/{id}` | Buscar membro por ID |
| `PUT` | `/membros/{id}` | Atualizar membro |
| `DELETE` | `/membros/{id}` | Remover membro |

### Células
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/celulas` | Listar células |
| `POST` | `/celulas` | Criar célula |
| `GET` | `/celulas/{id}` | Buscar célula |
| `PUT` | `/celulas/{id}` | Atualizar célula |

### Casa de Paz
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/casas-de-paz` | Listar Casas de Paz |
| `POST` | `/casas-de-paz` | Criar Casa de Paz |
| `GET` | `/casas-de-paz/{id}/relatorio/pdf` | Exportar relatório PDF |

### Outros módulos
Consulte a documentação completa em `/swagger-ui.html` após subir a aplicação.

---

## 🔐 Autenticação

A API utiliza **JWT Bearer Token** para autenticação stateless.

**1. Obtenha o token:**
```bash
curl -X POST https://seu-app.onrender.com/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "usuario@email.com", "senha": "sua_senha"}'
```

**2. Use o token nas requisições:**
```bash
curl https://seu-app.onrender.com/membros \
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

Tokens expiram em **24 horas** por padrão (configurável via `JWT_EXPIRATION`).

---

## 📁 Estrutura de Pastas

```
GestaoQuadrangular-Backend/
├── src/
│   └── main/
│       ├── java/com/gestaoigrejaemcelula/demo/
│       │   ├── aplication/
│       │   │   ├── dto/              # DTOs de request e response
│       │   │   └── service/          # Serviços de negócio
│       │   ├── domain/
│       │   │   ├── entity/           # Entidades JPA
│       │   │   └── repository/       # Repositórios Spring Data
│       │   ├── security/
│       │   │   └── config/           # JWT Filter e SecurityConfig
│       │   └── web/
│       │       └── controller/       # Controllers REST
│       └── resources/
│           └── application.properties
├── Dockerfile                        # Build multi-stage
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature: `git checkout -b feature/minha-feature`
3. Commit suas mudanças: `git commit -m 'feat: adiciona minha feature'`
4. Push para a branch: `git push origin feature/minha-feature`
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<p align="center">Feito com ☕ e muito amor pela Igreja ⛪</p>
