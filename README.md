# ⛪ GestaoQuadrangular — Backend

> Sistema de gestão eclesiástica para igrejas da **Igreja do Evangelho Quadrangular (IEQ)**.
> Gerencie células, membros, discipulado, tesouraria e muito mais — tudo em um só lugar.

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Hierarquia de Usuários](#hierarquia-de-usuários)
- [Módulos do Sistema](#módulos-do-sistema)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Entidades e Relacionamentos](#entidades-e-relacionamentos)
- [Endpoints da API](#endpoints-da-api)
- [Configuração e Execução](#configuração-e-execução)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Integrações](#integrações)
- [Convenções de Código](#convenções-de-código)

---

## 🌟 Visão Geral

O **GestaoQuadrangular** é um sistema backend RESTful desenvolvido para centralizar a gestão administrativa e espiritual de igrejas locais da IEQ. O sistema suporta múltiplos perfis de usuário com controle de acesso granular, permitindo que pastores, secretários e líderes de célula gerenciem suas responsabilidades de forma integrada.

### Funcionalidades principais

- ✅ Autenticação JWT com perfis de acesso (roles)
- ✅ Gestão de células, membros e visitantes
- ✅ Relatório semanal de célula (realizada / não realizada)
- ✅ Chamada de discipulado semanal (5 cultos)
- ✅ Missão 70 — Casa de Paz com 4 encontros
- ✅ Tesouraria — dízimos, ofertas e lançamentos
- ✅ Metas por célula com recalculo automático
- ✅ Ranking de células por frequência
- ✅ Alertas de ausência para o pastor
- ✅ Aniversariantes do mês
- ✅ Eventos da igreja
- ✅ Auditoria de ações sensíveis
- ✅ Notificações via WhatsApp (Meta Cloud API)
- ✅ Jornada espiritual do membro
- ✅ Ficha de encontro e solicitação de arrolamento

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3 |
| Segurança | Spring Security + JWT |
| Persistência | Spring Data JPA / Hibernate |
| Banco de Dados | PostgreSQL |
| Cache | Caffeine |
| Pool de Conexões | HikariCP |
| Build | Maven |
| Notificações | Meta WhatsApp Cloud API |
| Documentação | SpringDoc OpenAPI (Swagger) |

---

## 👤 Hierarquia de Usuários

```
ADMIN
  └── SUPERINTENDENTE
        └── PASTOR
              ├── SECRETARIO
              ├── TESOUREIRO
              └── LIDER_CELULA
                    └── (Célula → Membros + Visitantes)
```

### Perfis e permissões

| Perfil | Descrição | Principais Acessos |
|---|---|---|
| `ADMIN` | Desenvolvedor / administrador do sistema | Acesso total, auditoria global, gestão de usuários |
| `SUPERINTENDENTE` | Líder regional | Visão de todas as igrejas da região |
| `PASTOR` | Líder da igreja local | Dashboard, pendências, alertas, aprovações |
| `SECRETARIO` | Auxiliar administrativo | Células, membros, fichas, relatórios |
| `TESOUREIRO` | Responsável financeiro | Tesouraria, lançamentos, relatórios financeiros |
| `LIDER_CELULA` | Líder de célula | Relatório semanal, discipulado, visitantes |

---

## 📦 Módulos do Sistema

### 🔐 Autenticação (`/auth`)
Login com JWT, troca de senha, alteração de e-mail com aprovação pendente.

### 👥 Usuários (`/usuarios`)
CRUD de usuários com controle de perfil e ativação/desativação.

### ⛪ Células (`/celulas`)
Criação e gestão de células, solicitação de multiplicação, histórico.

### 🙋 Membros (`/membros`)
Ficha completa do membro: dados pessoais, espirituais, endereço, arrolamento, histórico de status.

### 👁️ Visitantes (`/visitantes`)
Cadastro de visitantes por célula, decisão espiritual, arquivamento.

### 📋 Relatório Semanal (`/relatorios`)
Envio semanal com presença de membros e visitantes, marcação de célula não realizada com motivo.

### 📖 Discipulado (`/discipulado`)
Chamada semanal com 5 cultos: Escola Bíblica, Quarta, Quinta, Domingo manhã e Domingo noite. Com justificativas por falta.

### 🕊️ Missão 70 (`/missao70`)
Casa de paz com 4 encontros semanais, registro de decisões espirituais por encontro.

### 🎯 Metas (`/metas`)
Metas mensais por célula (batismo, conversão, reconciliação, discipulado) com recalculo automático após envio de relatório.

### 🏆 Ranking (`/ranking`)
Células ordenadas por frequência e presença no período.

### 💰 Tesouraria (`/tesouraria`)
Lançamentos de dízimos e ofertas, classificados por tipo de oferta (BRONZE, PRATA, OURO).

### 📊 Dashboard do Pastor (`/pastor/dashboard`)
Visão consolidada: total de células, membros, visitantes, frequência, alertas e pendências.

### 🔔 Alertas (`/alertas`)
Membros com 2+ faltas nos cultos dominicais — gerado automaticamente e notificado ao pastor.

### 📅 Eventos (`/eventos`)
CRUD de eventos da igreja com lista de membros e visitantes participantes.

### 🎂 Aniversariantes (`/aniversarios`)
Listagem de membros aniversariantes por mês, com integração de lembrete via WhatsApp.

### 📁 Ficha de Encontro (`/fichas-encontro`)
Fichas de conversão preenchidas durante eventos ou células.

### 📝 Solicitações de Membro (`/solicitacoes-membro`)
Solicitações de arrolamento (profissão de fé / transferência) com fluxo de aprovação pelo pastor.

### 🛤️ Jornada Espiritual (`/jornada-espiritual`)
Acompanhamento individual do membro: batismo nas águas, batismo no Espírito Santo, liderança, discipulado.

### 🔗 Vínculo de Célula (`/vinculos-celula`)
Histórico de células pelas quais o membro passou.

### 📱 WhatsApp (`/whatsapp`)
Envio de lembretes e alertas via Meta Cloud API. Webhook para recebimento de confirmações.

### 🚫 Bloqueios (`/bloqueios`)
Números bloqueados de receber notificações via WhatsApp.

### 📜 Auditoria (`/auditoria`)
Log de todas as ações sensíveis do sistema (criação, alteração, exclusão).

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/gestaoigrejaemcelula/demo/
│
├── domain/
│   ├── entity/                   # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Celula.java
│   │   ├── Membro.java
│   │   ├── Visitante.java
│   │   ├── Relatorio.java
│   │   ├── DiscipuladoRelatorio.java
│   │   ├── DiscipuladoAcompanhamento.java
│   │   ├── Missao70.java
│   │   ├── EncontroMissao70.java
│   │   ├── DecisaoMissao70.java
│   │   ├── CasaDePaz.java
│   │   ├── EncontroCasaDePaz.java
│   │   ├── Meta.java
│   │   ├── LancamentoTesouraria.java
│   │   ├── Evento.java
│   │   ├── FichaEncontro.java
│   │   ├── SolicitacaoMembroFicha.java
│   │   ├── JornadaEspiritual.java
│   │   ├── HistoricoCelula.java
│   │   ├── HistoricoStatusMembro.java
│   │   ├── GrupoMissionario.java
│   │   ├── Presenca.java
│   │   ├── Notificacao.java
│   │   ├── NumeroBloqueado.java
│   │   ├── RegistroAuditoria.java
│   │   └── RegistroWebhook.java
│   │
│   ├── enums/                    # Enumerações do domínio
│   │   ├── Perfil.java           # ADMIN, PASTOR, LIDER_CELULA, SUPERINTENDENTE, SECRETARIO, TESOUREIRO
│   │   ├── StatusMembro.java     # ATIVO, AFASTADO, TRANSFERIDO, FALECIDO, INATIVO
│   │   ├── TipoArrolamento.java  # PROFISSAO_DE_FE, TRANSFERENCIA
│   │   ├── DecisaoEspiritual.java# ACEITOU_JESUS, RECONCILIOU, BATISMO_AGUAS, NENHUMA
│   │   ├── JustificativaFalta.java # DOENCA, TRABALHO, VIAGEM, OUTROS
│   │   ├── MotivoNaoRealizacaoCelula.java
│   │   ├── EstadoCivil.java
│   │   ├── Sexo.java
│   │   ├── OrigemVisitante.java
│   │   ├── StatusCasaDePaz.java
│   │   ├── StatusMissao70.java
│   │   ├── StatusSolicitacaoMembro.java
│   │   ├── Tipo.java
│   │   ├── TipoArrolamento.java
│   │   ├── TipoDiscipulado.java
│   │   ├── TipoEncontro.java
│   │   ├── TipoEvento.java
│   │   └── tipoOferta.java       # BRONZE, PRATA, OURO
│   │
│   └── repository/               # Interfaces JpaRepository
│
├── aplication/
│   ├── dto/                      # DTOs de Request e Response
│   └── service/                  # Regras de negócio
│       ├── AuthService.java
│       ├── UsuarioService.java
│       ├── CelulaService.java
│       ├── MembroService.java
│       ├── VisitanteService.java
│       ├── RelatorioService.java
│       ├── DiscipuladoRelatorioService.java
│       ├── DiscipuladoService.java
│       ├── Missao70Service.java
│       ├── CasaDePazService.java
│       ├── MetaService.java
│       ├── TesourariaService.java
│       ├── RankingCelulaService.java
│       ├── EventoService.java
│       ├── AlertaCelulaService.java
│       ├── AniversarioService.java
│       ├── FichaEncontroService.java
│       ├── JornadaEspiritualService.java
│       ├── SolicitacaoMembroFichaService.java
│       ├── VinculoCelulaService.java
│       ├── PastorDashboardService.java
│       ├── PastorPendenciasService.java
│       ├── NotificacaoService.java
│       ├── WhatsAppService.java
│       ├── WhatsAppWebhookService.java
│       ├── LembreteWhatsAppScheduler.java
│       ├── BloqueioService.java
│       ├── AuditoriaService.java
│       └── AuditoriaHelper.java
│
├── web/
│   ├── controller/               # Endpoints REST
│   └── handler/
│       ├── GlobalExceptionHandler.java
│       ├── BusinessException.java
│       ├── ResourceNotFoundException.java
│       └── ErrorResponse.java
│
└── security/
    └── config/                   # JWT, SecurityConfig, OpenApiConfig
```

---

## 🗃️ Entidades e Relacionamentos

### Usuario
```
id, nome, email, senha (BCrypt), perfil (enum), ativo,
fotoPerfil (TEXT), emailPendente, senhaPendente,
telefoneWhatsapp, celula (FK)
```

### Celula
```
id, nome, lider (FK Usuario), pastor (FK Usuario),
secretario (FK Usuario), anfitriao, endereco, bairro,
diaSemana (DayOfWeek), horario (LocalTime), ativa,
statusMultiplicacao (enum), dataSolicitacaoMultiplicacao,
motivoSolicitacao, membros (List<Membro>)
```

### Membro
```
id, nome, telefone, email, cpf, rg, estadoCivil, dataNascimento,
dataConversao, dataBatismo, dataCadastro, status (enum),
celula (FK), nomeMae, nomePai, nomeConjuge, naturalidade,
grauEscolaridade, curso, profissao, endereco, numero, bairro,
cidade, cep, uf, pertenceOutraReligiao, qualReligiao,
batizadoNasAguas, dataBatizadoNasAguas, igrejaBatizadoNasAguas,
batizadoEspiritoSanto, tipoArrolamento (enum),
jurisdicaoArrolamento, arroladoPor, observacoes
```

### Visitante
```
id, nome, telefone, email, dataPrimeiraVisita,
origem (enum), responsavelAcompanhamento,
decisaoEspiritual (enum), convertido, ativo, arquivado,
dataArquivamento, celula (FK)
```

### Relatorio
```
id, celula (FK), dataReuniao, estudo (TEXT),
quantidadeVisitantes, realizada, motivoNaoRealizacao (enum),
presentes (List<Membro>), visitantesPresentes (Set<Visitante>),
dataCadastro
```

### DiscipuladoRelatorio
```
id, celula (FK), membro (FK), lider (FK),
semanaInicio, semanaFim,
escolaBiblica, quartaNoite, quintaNoite,
domingoManha, domingoNoite (boolean),
justEscolaBiblica, justQuartaNoite, justQuintaNoite,
justDomingoManha, justDomingoNoite (String)
```

### Missao70
```
id, nome, nomeAnfitriao, endereco, telefoneContato,
dataInicio, encontrosRestantes (int, default 4),
proximaSemana (int), status (enum),
celula (FK), lider (FK Membro), auxiliar (FK Membro),
visitantes (List<Visitante>), encontros (List<EncontroMissao70>)
```

### Meta
```
id, celula (FK), tipoMeta (BATISMO/CONVERSAO/RECONCILIACAO/DISCIPULADO),
metaTotal, metaAlcancada, mesAno (LocalDate), ativa,
dataCriacao, descricao
```

### LancamentoTesouraria
```
id, membroNome, valorDizimo (BigDecimal),
valorOferta (BigDecimal), tipoOferta (BRONZE/PRATA/OURO),
dataLancamento
```

### Evento
```
id, nome, tipo (enum TipoEvento), data, local, anfitriao,
membros (List<Membro>), visitantes (List<Visitante>),
totalPresentes, aceitaramJesus, batizados
```

---

## 🔌 Endpoints da API

### Auth
```
POST   /auth/login
POST   /auth/refresh
POST   /auth/logout
PUT    /auth/senha
PUT    /auth/email/solicitar
```

### Usuários
```
POST   /usuarios
GET    /usuarios
GET    /usuarios/{id}
PUT    /usuarios/{id}
DELETE /usuarios/{id}
PUT    /usuarios/{id}/ativar
PUT    /usuarios/{id}/desativar
GET    /perfil
PUT    /perfil
PUT    /perfil/foto
```

### Células
```
POST   /celulas
GET    /celulas
GET    /celulas/{id}
GET    /celulas/minha-celula
PUT    /celulas/{id}
DELETE /celulas/{id}
GET    /celulas/{id}/membros
POST   /celulas/{id}/solicitar-multiplicacao
GET    /vinculos-celula/membro/{membroId}
```

### Membros
```
POST   /membros
GET    /membros
GET    /membros/{id}
PUT    /membros/{id}
DELETE /membros/{id}
PUT    /membros/{id}/status
GET    /membros/aniversariantes
```

### Visitantes
```
POST   /visitantes
GET    /visitantes/celula/{celulaId}/ativos
GET    /visitantes/{id}
PUT    /visitantes/{id}
DELETE /visitantes/{id}
PUT    /visitantes/{id}/decisao
```

### Relatório Semanal
```
POST   /relatorios
POST   /relatorios/nao-realizada
GET    /relatorios/historico
GET    /relatorios/{id}
PUT    /relatorios/{id}
GET    /relatorios/todos
```

### Discipulado
```
POST   /discipulado/relatorio-semanal
GET    /discipulado/relatorio-semanal
GET    /discipulado/relatorio-semanal/{id}
PUT    /discipulado/relatorio-semanal/{id}
PUT    /discipulado/{id}
GET    /discipulado/historico
GET    /discipulado/semana/{id}
```

### Missão 70
```
POST   /missao70
GET    /missao70
GET    /missao70/{id}
PUT    /missao70/{id}
DELETE /missao70/{id}
POST   /missao70/{id}/encontros
GET    /relatorios/missao70
GET    /relatorios/missao70/{id}
```

### Metas
```
POST   /metas
GET    /metas/celula/{celulaId}
PUT    /metas/celula/{celulaId}/recalcular
PUT    /metas/{id}
DELETE /metas/{id}
```

### Ranking
```
GET    /ranking/celulas
```

### Tesouraria
```
POST   /tesouraria
GET    /tesouraria
GET    /tesouraria/{id}
PUT    /tesouraria/{id}
DELETE /tesouraria/{id}
```

### Dashboard & Alertas
```
GET    /pastor/dashboard
GET    /pastor/pendencias
GET    /alertas/celula/{celulaId}
PUT    /alertas/{id}/resolver
```

### Eventos & Aniversários
```
POST   /eventos
GET    /eventos
GET    /eventos/{id}
PUT    /eventos/{id}
DELETE /eventos/{id}
GET    /aniversarios
GET    /aniversarios/mes/{mes}
```

### Fichas & Solicitações
```
POST   /fichas-encontro
GET    /fichas-encontro
GET    /fichas-encontro/{id}
POST   /solicitacoes-membro
GET    /solicitacoes-membro
PUT    /solicitacoes-membro/{id}/decisao
```

### Auditoria
```
GET    /auditoria
GET    /auditoria/usuario/{id}
```

### WhatsApp
```
GET    /whatsapp/webhook              (verificação)
POST   /whatsapp/webhook              (recebimento)
POST   /whatsapp/lembrete/relatorio
GET    /whatsapp/registros
POST   /bloqueios
GET    /bloqueios
DELETE /bloqueios/{id}
```

---

## ⚙️ Configuração e Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/GestaoQuadrangular-Backend.git
cd GestaoQuadrangular-Backend
```

### 2. Configure o banco de dados

```sql
CREATE DATABASE gestao_quadrangular;
```

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` ou exporte as variáveis:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gestao_quadrangular
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=sua_senha
export JWT_SECRET=seu_segredo_jwt_aqui
export JWT_EXPIRATION=86400000
export WHATSAPP_API_TOKEN=seu_token_meta
export WEBHOOK_VERIFY_TOKEN=QuadrangularWebhook2026
```

### 4. Execute a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🔑 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL do banco PostgreSQL | — |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | — |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | — |
| `JWT_SECRET` | Chave secreta do JWT | — |
| `JWT_EXPIRATION` | Expiração do token em ms | `86400000` (24h) |
| `WHATSAPP_API_TOKEN` | Token da Meta Cloud API | — |
| `WHATSAPP_PHONE_NUMBER_ID` | ID do número WhatsApp | `1172895332578283` |
| `WHATSAPP_API_VERSION` | Versão da API Meta | `v23.0` |
| `WEBHOOK_VERIFY_TOKEN` | Token de verificação do webhook | `QuadrangularWebhook2026` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo | `dev` |
| `PORT` | Porta do servidor | `8080` |

---

## 📱 Integrações

### WhatsApp — Meta Cloud API

O sistema usa a **Meta WhatsApp Cloud API** para envio de notificações:

- **Lembrete semanal** para líderes enviarem o relatório
- **Alertas de ausência** para o pastor
- **Aniversários** dos membros
- **Comunicados** da liderança

Template padrão utilizado: `notificacao_geral`

Configuração do Webhook:
```
URL:           https://seu-dominio.com/whatsapp/webhook
Verify Token:  QuadrangularWebhook2026
```

### Scheduler — Lembretes Automáticos

O `LembreteWhatsAppScheduler` dispara automaticamente lembretes de relatório para todos os líderes de célula com WhatsApp cadastrado.

---

## 📐 Convenções de Código

- `@Transactional(readOnly = true)` em todos os métodos de consulta
- `@PreAuthorize("hasAnyRole(...)")` em todos os endpoints com controle de acesso
- DTOs separados para Request e Response
- Paginação com `Page<T>` + `Pageable` em listagens grandes
- `JOIN FETCH` em queries com relacionamentos para evitar N+1
- `countQuery` separado em queries com `JOIN FETCH` + `Pageable` (evita `HHH90003004`)
- Tratamento centralizado de erros via `GlobalExceptionHandler`
- Códigos de erro customizados: `DUPLICATE_REPORT`, `MEMBER_NOT_FOUND`, etc.
- Senhas armazenadas com **BCrypt**
- Datas como `LocalDate` / `LocalDateTime`
- Fuso horário: `America/Sao_Paulo`
- Cache com **Caffeine** (TTL: 3 minutos, máximo 30 entradas)

---

## 🚀 Roadmap

- [ ] Entidades `Regiao` e `Igreja` para suporte multi-igreja
- [ ] Perfil `SUPERINTENDENTE` com dashboard regional
- [ ] Isolamento de dados por `igrejaId` em todos os módulos
- [ ] Migração para microsserviços (auth, cell, report, finance, notify)
- [ ] App mobile (React Native) para líderes
- [ ] Exportação de relatórios em PDF / Excel
- [ ] QR Code de presença na célula
- [ ] Integração com PIX para tesouraria

---

*Igreja do Evangelho Quadrangular — Avante, sem parar! 🔥*
