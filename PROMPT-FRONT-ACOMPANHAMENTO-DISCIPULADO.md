# Implementar Frontend — Módulo de Acompanhamento de Discipulado (Telas do Líder e do Pastor)

## Contexto
O backend (Spring Boot, JWT Bearer) já está pronto. Criar as telas do módulo "Acompanhamento de Discipulado" seguindo os padrões visuais e de arquitetura já usados no projeto (mesma lib de HTTP com interceptor de token, mesmos componentes de tabela/modal/toast).

Perfis que acessam: **LIDER_CELULA**, **PASTOR**, **ADMIN**.

---

## Menu
Adicionar ao menu lateral (visível conforme perfil):

**Acompanhamento**
→ Discipulado Individual *(líder)*
→ Discipulado Coletivo *(líder)*
→ Histórico *(líder / pastor / admin)*
→ Indicadores *(líder)*
→ Relatórios das Células *(apenas PASTOR/ADMIN)*

---

## BASE DA API
Base: `/api/acompanhamento/discipulado` (header `Authorization: Bearer <token>`)

Enums:
- `tipoEstudo`: ESTUDO_BIBLICO | ACOMPANHAMENTO | VIDA_CRISTA | ORACAO | NOVO_CONVERTIDO | LIDERANCA | FAMILIA | RELACIONAMENTO_COM_DEUS | OUTRO
  (sempre enviar `tipoEstudoOutro` quando OUTRO)
- `status`: CONCLUIDO | CANCELADO

Datas: `YYYY-MM-DD` · Horários: `HH:mm`

---

## 1) DASHBOARD DO LÍDER (`GET /indicadores`)
Cards no topo:
- Pontos esta semana → `pontosDiscipuladoSemana`
- Pontos este mês → `pontosDiscipuladoMes`
- Individuais esta semana → `discipuladosIndividuaisSemana`
- Individuais este mês → `discipuladosIndividuaisMes`
- Coletivos este mês / total → `discipuladosColetivosMes` / `discipuladosColetivosTotal`
- Participações em coletivos → `participacoesColetivasTotal`
- Total de discipulados → `totalDiscipulados`
- Total de pontos → `totalPontos`
- Membros discipulados → `membrosDiscipulados`
- Pendentes nesta semana → `membrosNaoDiscipuladosSemana` (+ lista `nomesMembrosPendentesSemana`)

Botões de ação: **+ Registrar Discipulado Individual** e **+ Registrar Discipulado Coletivo**.

---

## 2) DISCIPULADO INDIVIDUAL

### Lista de membros da célula — `GET /membros`
Tabela:

| Membro | Último discipulado | Total individuais | Participações coletivas | Pontos | Status semanal |
|---|---|---|---|---|---|

`statusSemanal`: "REALIZADO" → badge verde ✅ · "PENDENTE" → badge vermelho ❌
Campos por linha: `membroId`, `membroNome`, `ultimoDiscipulado`, `totalDiscipuladosIndividuais`, `participacoesColetivas`, `totalPontos`, `discipuladoEstaSemana`, `proximoPeriodoDisponivel`, `mensagemStatus`.

Ao clicar num membro com ✅: mostrar painel/modal informativo com `mensagemStatus`, `ultimoDiscipulado` e texto **"Pontuação gerada: 5 pontos"**.
Botão "+ Registrar" fica **desabilitado** quando `discipuladoEstaSemana === true`.

### Modal de registro (`POST /individual`)
Request:

```json
{
  "membroId": 1,
  "data": "2026-08-23",
  "horario": "19:30",
  "tipoEstudo": "ESTUDO_BIBLICO",
  "tipoEstudoOutro": null,
  "tema": "Fé e confiança em Deus",
  "observacoes": "...",
  "local": "Igreja"
}
```

Resposta 201 → toast com o campo `mensagem` do corpo:

> "Discipulado registrado com sucesso! +5 pontos para a célula."

Erro 409 (errorCode `MEMBRO_JA_DISCIPULADO_SEMANA`) → exibir `message` exatamente:

> "Este membro já foi discipulado nesta semana. Um novo discipulado individual poderá ser registrado somente na próxima semana."

Validações client-side: data ≤ hoje (max=today); horário obrigatório; tema obrigatório; OUTRO exige `tipoEstudoOutro`. **Nunca enviar campo de pontos.**

### Histórico do membro — `GET /individual/historico/{membroId}`
Header com `totalDiscipulados`, `totalPontos`, `ultimoDiscipulado`;
timeline/lista com cada item: data+horário, tipo de estudo (usar `tipoEstudoDescricao`), tema, observações, local, status, `pontosGerados`.
Botão "Cancelar" (PATCH `/individual/{id}/cancelar`, confirmar antes) quando status=CONCLUIDO.

---

## 3) DISCIPULADO COLETIVO

### Modal de registro (`POST /coletivo`)
Formulário do encontro: data (≤ hoje), horário, tipo de estudo (+OUTRO), tema, local, observações.
Seção **Participantes**: checklist com todos os membros ativos (dados de `GET /membros`) + checkbox master **"Selecionar todos os presentes"**.
Preview ao vivo da pontuação: `quantidadeMarcados × 5 = X pontos`.

Request:

```json
{
  "data": "2026-08-20",
  "horario": "19:30",
  "tipoEstudo": "ORACAO",
  "tipoEstudoOutro": null,
  "tema": "Fé",
  "local": "Igreja",
  "observacoes": "...",
  "participantesIds": [1, 2, 3]
}
```

Resposta 201 → toast com `mensagem`:

> "Discipulado coletivo registrado com sucesso! 8 participantes foram contabilizados. +40 pontos para a célula."

Bloquear submit sem nenhum marcado (backend rejeita com 409).

### Detalhes — `GET /coletivo/{id}`
Mostrar dados do encontro + lista numerada dos presentes (`presentes[].membroNome`) + `formulaPontuacao` ("8 × 5 = 40") + `quantidadePresentes` + observações.
Botão cancelar (PATCH `/coletivo/{id}/cancelar`) quando CONCLUIDO.

---

## 4) HISTÓRICO GERAL — `GET /historico`
Filtros: período (`dataInicio`, `dataFim`), `membroId`, tipo (INDIVIDUAL/COLETIVO), tema (texto), tipoEstudo (select).
Tabela:

| Data | Tipo | Célula | Membro/Participantes | Tema | Líder | Status | Pontos |
|---|---|---|---|---|---|---|---|

- Individual → coluna membro mostra `membroNome`
- Coletivo → mostra "Coletivo · N participantes" clicável → abre modal `GET /coletivo/{id}`

---

## 5) TELA DO PASTOR (PASTOR/ADMIN)
Select de células populado por `GET /pastor/celulas` (`id`, `nome`, `liderNome`, `qtdMembros`) — ADMIN vê todas as células; sem seleção = todas.
Mesma tabela/filtros do Histórico Geral consumindo `GET /pastor/historico` (mesmos filtros + `celulaId`). Linha coletiva abre o mesmo modal de detalhes.

---

## Tratamento de erros (padrão do backend)
- 409 `{status, title, message, errorCode}` → toast/inline com `message`. `MEMBRO_JA_DISCIPULADO_SEMANA` tem tratamento especial (msg acima).
- Outras mensagens de negócio vêm em 409 genéricas (ex.: "Este membro não pertence à sua célula").
- 422 validação → listar erros de campo.
- Após salvar/cancelar: recarregar dashboard, lista de membros e histórico.

---

## Fluxos rápidos
Individual: Dashboard → Selecionar membro → Preencher → Salvar → toast +5 pts.
Coletivo: Dashboard → Novo encontro → Preencher → Marcar presentes → Salvar → toast N×5.
